package cn.id0755.im.manager;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.config.Config;
import cn.id0755.im.test.ProtocolClientHandler;
import cn.id0755.im.utils.RandomUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ConnectionManager {
    private final static String TAG = ConnectionManager.class.getSimpleName();

    private ConnectionManager() {
        //todo
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

    private volatile boolean bConnect = false;

    public boolean isConnect() {
        return bConnect;
    }

    private final static int MAX_RECONNECT_COUNT = 10;
    private int mReConnectCount = 0;

//    public static void main(String[] args) throws Exception {
//        new ConnectionManager().connect(Config.PORT, Config.HOST);
//    }

    public void connect() {
        try {
            connect(Config.PORT, Config.HOST);
        } catch (Exception e) {
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
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(Message.MessageData.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ProtocolClientHandler());
                        }
                    });

            // 发起异步连接操作
            ChannelFuture f = b.connect(host, port).addListener(new ConnectionListener()).sync();
            // 当代客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    private class ConnectionListener implements ChannelFutureListener {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {

            } else {
                if (mReConnectCount >= MAX_RECONNECT_COUNT) {
                    return;
                }
                mReConnectCount++;
                EventLoop eventLoop = future.channel().eventLoop();
                eventLoop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //启动重连
                        connect();
                    }
                }, RandomUtil.randInt(1, 2 + mReConnectCount), TimeUnit.SECONDS);
            }
        }
    }

}
