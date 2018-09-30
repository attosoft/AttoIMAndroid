package cn.id0755.sdk.android.manager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.id0755.im.IMessageService;
import cn.id0755.im.ITaskWrapper;
import cn.id0755.sdk.android.config.TaskProperty;
import cn.id0755.sdk.android.service.MsgRemoteService;
import cn.id0755.sdk.android.utils.Log;

public class MessageServiceManager {
    private final static String TAG = MessageServiceManager.class.getSimpleName();

    private MessageServiceManager() {
        init();
    }

    private ThreadPoolExecutor mRemoteExecutor = new ThreadPoolExecutor(0, 1, 10,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
            new ThreadFactory() {
                private int mCount = 0;

                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, "MessageServiceManager " + mCount++);
                }
            },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Log.w(TAG, "mRemoteExecutor rejectedExecution");
                    r.run();
                }
            });
    private WorkerThread mWorkerThread = new WorkerThread();
    private LinkedBlockingQueue<ITaskWrapper> mTaskQueue = new LinkedBlockingQueue<>();
    /**
     * 多线程会使用到mMessageService，需要同步mMessageService状态
     */
    private final Object mLock = new Object();
    /**
     * MessageServer 的binder代理对象
     */
    private IMessageService mMessageService = null;
    private static volatile MessageServiceManager mInstance = null;

    public static MessageServiceManager getInstance() {
        if (mInstance == null) {
            synchronized (MessageServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new MessageServiceManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 死亡代理
     */
    private IBinder.DeathRecipient mMessageServiceDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            synchronized (mLock) {
                if (mMessageService == null) {
                    return;
                }
                mMessageService.asBinder().unlinkToDeath(mMessageServiceDeathRecipient, 0);
                mMessageService = null;
            }
            /* 已断开连接，重连 */
            bindMessageService();
        }
    };

    /**
     * 绑定到远程服务
     */
    private ServiceConnection mMessageServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected 绑定到远程MessageService");
            synchronized (mLock) {
                mMessageService = IMessageService.Stub.asInterface(service);
                try {
                    /*连接死亡代理*/
                    mMessageService.asBinder().linkToDeath(mMessageServiceDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onServiceConnected 连接死亡代理出错：" + e.getMessage());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected 断开到远程MessageService连接");
            synchronized (mLock) {
                if (mMessageService == null) {
                    return;
                }
                mMessageService.asBinder().unlinkToDeath(mMessageServiceDeathRecipient, 0);
                mMessageService = null;
            }
        }
    };

    private void init() {
        bindMessageService();
        mWorkerThread.start();
    }

    private void bindMessageService() {
        Context context = ImApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(context, MsgRemoteService.class);
        context.startService(intent);
        if (!context.bindService(intent, mMessageServiceConnection, Service.BIND_AUTO_CREATE)) {
            Log.e(TAG, "remote message service bind failed");
        }
    }

    private void processMessageTask() throws InterruptedException {
        synchronized (mLock) {
            if (mMessageService == null) {
                bindMessageService();
                return;
            }
        }
        ITaskWrapper taskWrapper = mTaskQueue.take();
        if (taskWrapper == null) {
            return;
        }
        synchronized (mLock) {
            if (mMessageService == null) {
                if (!mTaskQueue.offer(taskWrapper)) {
                    Log.e(TAG, "与MessageService连接断开，任务添加回队列出错！");
                    try {
                        //回调任务错误原因
                        taskWrapper.onTaskEnd(0, 0);
                    } catch (RemoteException e) {
                        //本地进程不应该出现RemoteException
                        Log.e(TAG, "严重错误，不应该出现（运行到其他进程中了。。。） ： " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "与MessageService连接断开，任务添加回队列成功!");
                }
                return;
            }
            try {
                //远程调用阻塞线程
                mMessageService.send(taskWrapper, taskWrapper.getProperties());
            } catch (RemoteException e) {
                Log.e(TAG, "调用MessageService发送任务异常！");
                //尝试添加回任务队列。
                if (!mTaskQueue.offer(taskWrapper)) {
                    Log.e(TAG, "调用MessageService发送任务异常，任务添加回队列出错！");
                    try {
                        //回调任务错误原因
                        taskWrapper.onTaskEnd(0, 0);
                    } catch (RemoteException remoteException) {
                        //本地进程不应该出现RemoteException
                        Log.e(TAG, "严重错误，不应该出现（运行到其他进程中了。。。） ： " + remoteException.getMessage());
                        remoteException.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "调用MessageService发送任务异常，任务添加回队列成功!");
                }
                e.printStackTrace();
            }
        }
    }

//    private void sendMessage(AbstractMessageLite messageLite) {
//        Message.MessageData messageData = MessageUtil.wrap(messageLite);
//    }

    /**
     * 只代表添加到任务队列是否成功
     *
     * @param taskWrapper
     * @return
     */
    public boolean send(ITaskWrapper taskWrapper) {
        return mTaskQueue.offer(taskWrapper);
    }

    /**
     * 使用mRemoteExecutor独立线程执行，防止远程调用阻塞调用线程
     *
     * @param taskWrapper
     */
    public void cancel(final ITaskWrapper taskWrapper) {
        mRemoteExecutor.submit(new Runnable() {
            @Override
            public void run() {
                cancelTask(taskWrapper);
            }
        });
    }

    /**
     * 取消任务
     *
     * @param taskWrapper 要取消的任务
     * @return true 取消成功，false 取消失败
     */
    private boolean cancelTask(ITaskWrapper taskWrapper) {
        boolean success = false;
        if (mTaskQueue.remove(taskWrapper)) {
            // Remove from queue, not exec yet, call ITaskWrapper::onTaskEnd
            try {
                taskWrapper.onTaskEnd(-1, -1);
            } catch (RemoteException e) {
                Log.e(TAG, "cancel task wrapper in client, should not catch RemoteException");
                e.printStackTrace();
            }
            Log.d(TAG, "cancel Task:" + taskWrapper.toString());
            success = true;
        } else {
            // Already sent to remote service, need to cancel it
            synchronized (mLock) {
                if (mMessageService == null) {
                    return false;
                }
                try {
                    mMessageService.cancel(taskWrapper.getProperties().getInt(TaskProperty.OPTIONS_TASK_ID));
                    success = true;
                } catch (RemoteException e) {
                    /* 取消MessageServer 上的task失败*/
                    Log.w(TAG, "cancel task wrapper in remote service failed, I'll make TaskWrapper.onTaskEnd");
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * MessageServiceManager工作线程，循环获取任务队列中的任务发送到MessageServer执行
     */
    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    processMessageTask();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    /* processMessageTask() 获取阻塞队列没有任务时阻塞，
                    mMessageService为空时会走到这里，sleep 100ms 等待重新绑定到MessageServer */
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
