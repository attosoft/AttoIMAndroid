package cn.id0755.sdk.android.manager;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.protobuf.InvalidProtocolBufferException;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.config.Config;
import cn.id0755.sdk.android.config.TaskProperty;
import cn.id0755.sdk.android.handler.LoginHandler;
import cn.id0755.sdk.android.handler.PongHandler;
import cn.id0755.sdk.android.handler.PushHandler;
import cn.id0755.sdk.android.manager.iinterface.IChannelListener;
import cn.id0755.sdk.android.manager.iinterface.IPushMessageListener;
import cn.id0755.sdk.android.manager.iinterface.IServerConnectionListener;
import cn.id0755.sdk.android.handler.ProtocolClientHandler;
import cn.id0755.sdk.android.utils.Log;
import cn.id0755.sdk.android.utils.MessageUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ConcurrentSet;

public class ConnectionManager {
    private final static String TAG = ConnectionManager.class.getSimpleName();

    private ConnectionManager() {
        //todo
        mSendMsgExecutor.execute(mSendMsgRunnable);
    }

    private static volatile ConnectionManager mInstance = null;

    public static ConnectionManager getInstance() {
        if (mInstance == null) {
            synchronized (ConnectionManager.class) {
                if (mInstance == null) {
                    mInstance = new ConnectionManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 起一线程管理，因为会调用后会阻塞啊
     */
    private ThreadPoolExecutor mConnectExecutor = new ThreadPoolExecutor(0, 2, 10,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ConnectExecutor");
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Log.e(TAG, "ConnectExecutor rejectedExecution");
        }
    });

    private LinkedBlockingQueue<ITaskWrapper> mTaskQueue = new LinkedBlockingQueue<>();
    /**
     * 通过channel读写数据
     */
    private ThreadPoolExecutor mSendMsgExecutor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, "mSendMsgExecutor");
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    Log.e(TAG,"mSendMsgExecutor | uncaughtException:" + e.getMessage());
                    mSendMsgExecutor.execute(mSendMsgRunnable);
                }
            });
            return thread;
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Log.e(TAG, "SendMsgExecutor rejectedExecution");
        }
    });

    public static final String KEY_TASK = "key_task";
    public final static ThreadPoolExecutor mWorkerExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "mWorkerExecutor");
        }
    });

    private Runnable mSendMsgRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                Log.d(TAG, "mSendMsgRunnable run");
                ITaskWrapper taskWrapper = null;
                try {
                    taskWrapper = mTaskQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (taskWrapper == null) {
                    return;
                }
                synchronized (mChannelLock) {
                    if (!isConnected()) {
                        mTaskQueue.offer(taskWrapper);
                    } else {
                        try {
                            //远程调用会阻塞当前线程
                            final byte[] data = taskWrapper.req2buf();
                            Message.MessageData messageData = null;
                            try {
                                messageData = Message.MessageData.getDefaultInstance()
                                        .getParserForType()
                                        .parseFrom(data, 0, data.length);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                            if (messageData == null) {
                                return;
                            }
                            String seqId = messageData.getSeqId();
                            Attribute<Map<String, ITaskWrapper>> taskAttribute = mChannel.attr(AttributeKey.valueOf(KEY_TASK));
                            if (taskAttribute.get() == null) {
                                taskAttribute.set(new ConcurrentHashMap<>());
                            }
                            taskAttribute.get().put(seqId, taskWrapper);

                            Message.MessageData finalMessageData = messageData;
                            mChannel.eventLoop().submit(new Runnable() {
                                @Override
                                public void run() {
                                    mChannel.writeAndFlush(finalMessageData);
                                }
                            });
                        } catch (RemoteException e) {
                            Log.e(TAG, "mSendMsgRunnable | " + e.getMessage());
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }
            }
        }
    };
    /**
     * 是否连接中
     */
    private volatile boolean bConnecting = false;
    /**
     * Netty 的channel线程安全，可以保存一份
     */
    private Channel mChannel = null;
    /**
     * channel 锁
     */
    private final Object mChannelLock = new Object();

    private IServerConnectionListener mServerConnectionListener = null;

    private boolean isConnected() {
        synchronized (mChannelLock) {
            return mChannel != null && mChannel.isActive();
        }
    }

    /**
     * 自动连接
     */
    public void autoConnect(IServerConnectionListener listener) {
        mServerConnectionListener = listener;
        if (mServerConnectionListener != null) {
            if (bConnecting) {
                mServerConnectionListener.onConnectState(ConnectState.CONNECTING);
            } else {
                mServerConnectionListener.onConnectState(isConnected() ? ConnectState.CONNECTED : ConnectState.DISCONNECT);
            }
        }
        mConnectExecutor.submit(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
    }

    private void connect() {
        if (isConnected() || bConnecting) {
            return;
        }
        bConnecting = true;
        if (mServerConnectionListener != null) {
            mServerConnectionListener.onConnectStateChange(ConnectState.CONNECTING);
        }
        try {
            connect(Config.PORT, Config.HOST);
        } catch (Exception e) {
            Log.e(TAG, "连接远程netty服务器异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ProtocolClientHandler protocolClientHandler = new ProtocolClientHandler(mChannelListener);
                            protocolClientHandler.addBizHandler(new LoginHandler());
                            protocolClientHandler.addBizHandler(new PongHandler());
                            protocolClientHandler.addBizHandler(new PushHandler(mPushMessageListener));
                            /** 添加读写超时，产生idle事件，实现心跳机制;可以更智能一点，不固定收到就发ping，10s -- 10分钟之间*/
                            ch.pipeline().addLast(new IdleStateHandler(30, 30, 30));
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(Message.MessageData.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(protocolClientHandler);
                        }
                    });

            // 发起异步连接操作
            ChannelFuture f = b.connect(host, port).addListener(new ConnectionListener()).sync();
            // 当代客户端链路关闭
            f.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        if (mServerConnectionListener != null) {
                            mServerConnectionListener.onConnectStateChange(ConnectState.DISCONNECT);
                        }
                    } else {
                        //todo
                    }
                }
            }).sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
            bConnecting = false;
        }
    }

    private IPushMessageListener mPushMessageListener = null;
    public void setPushMessageListener(IPushMessageListener listener){
        mPushMessageListener = listener;
    }

    /**
     * 监听channel 状态
     */
    private IChannelListener mChannelListener = new IChannelListener() {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {

        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            synchronized (mChannelLock) {
                mChannel = null;
            }
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            synchronized (mChannelLock) {
                mChannel = null;
            }
        }
    };

    private class ConnectionListener implements ChannelFutureListener {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                Log.d(TAG, "ConnectionListener " + "success");
                synchronized (mChannelLock) {
                    mChannel = future.channel();
                }
                bConnecting = false;
                if (mServerConnectionListener != null) {
                    mServerConnectionListener.onConnectStateChange(ConnectState.CONNECTED);
                }
            } else {
                Log.d(TAG, "ConnectionListener " + "false");
                future.cause().printStackTrace();
                if (mServerConnectionListener != null) {
                    mServerConnectionListener.onConnectStateChange(ConnectState.DISCONNECT);
                }
            }
        }
    }

    public boolean send(ITaskWrapper taskWrapper) {
        Log.d(TAG, "send : ");
        //存放到队列中是为了：长链可能断开，需要连接状态才能发消息。
        return mTaskQueue.offer(taskWrapper);
    }

    public void cancel(int taskId) {
        Log.d(TAG, "cancel | taskId:" + taskId);
//        for (ITaskWrapper pair : mTaskQueue) {
//            if (pair.first == taskId) {
//                mTaskQueue.remove(pair);
//                return;
//            }
//        }
    }
}
