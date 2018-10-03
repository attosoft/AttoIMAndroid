package cn.id0755.sdk.android.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cn.id0755.im.IMessageService;
import cn.id0755.im.IPushMessageFilter;
import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.chat.proto.Push;
import cn.id0755.sdk.android.config.TaskProperty;
import cn.id0755.sdk.android.entity.DeviceInfo;
import cn.id0755.sdk.android.manager.ConnectState;
import cn.id0755.sdk.android.manager.ConnectionManager;
import cn.id0755.sdk.android.manager.iinterface.IPushMessageListener;
import cn.id0755.sdk.android.manager.iinterface.IServerConnectionListener;
import cn.id0755.sdk.android.utils.Log;
import cn.id0755.sdk.android.utils.RandomUtil;

/**
 * 运行于 :messageService进程中 自动处理与服务器的长连接
 */
public class MsgRemoteServiceStub extends IMessageService.Stub {
    private final static String TAG = MsgRemoteServiceStub.class.getSimpleName();
    public static final String DEVICE_NAME = android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL;
    public static final String DEVICE_TYPE = "android-" + android.os.Build.VERSION.SDK_INT;
    private DeviceInfo info = new DeviceInfo(DEVICE_NAME, DEVICE_TYPE);
    private Context mContext;

    private List<IPushMessageFilter> mMessageFilters = new CopyOnWriteArrayList<>();

    private Handler mHandler = new Handler();
    private final static AtomicInteger ai = new AtomicInteger();

    private IServerConnectionListener mConnectionListener = new IServerConnectionListener() {
        @Override
        public void onConnectState(ConnectState type) {
            Log.d(TAG, "IServerConnectionListener | onConnectState | type:" + type);
        }

        @Override
        public void onConnectStateChange(ConnectState type) {
            Log.d(TAG, "IServerConnectionListener | onConnectStateChange | type:" + type);
            switch (type) {
                case CONNECTED:
                    break;
                case CONNECTING:
                    break;
                case DISCONNECT:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "IServerConnectionListener " + " DISCONNECT " + "开始重连");
                            ConnectionManager.getInstance().autoConnect(mConnectionListener);
                        }
                    }, RandomUtil.randInt(1, 5));
                    break;
                default:
                    break;
            }
        }
    };

    private IPushMessageListener mPushMessageListener = new IPushMessageListener() {
        @Override
        public void pushMessage(Push.Message message) {
            for (IPushMessageFilter filter : mMessageFilters) {
                try {
                    filter.onReceive(Message.CMD_ID.PUSH_VALUE, message.toByteArray());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public MsgRemoteServiceStub(Context context) {
        mContext = context;
        ConnectionManager.getInstance().autoConnect(mConnectionListener);
        ConnectionManager.getInstance().setPushMessageListener(mPushMessageListener);
    }

    @Override
    public int send(ITaskWrapper taskWrapper, Bundle taskProperties) throws RemoteException {
        //运行于binder线程
        int taskId = ai.incrementAndGet();
        ConnectionManager.getInstance().send(taskWrapper);
        return taskId;
    }

    @Override
    public void cancel(int taskID) throws RemoteException {
        ConnectionManager.getInstance().cancel(taskID);
    }

    @Override
    public void registerPushMessageFilter(IPushMessageFilter filter) throws RemoteException {
        mMessageFilters.add(filter);
    }

    @Override
    public void unregisterPushMessageFilter(IPushMessageFilter filter) throws RemoteException {
        mMessageFilters.remove(filter);
    }

    @Override
    public void setAccountInfo(long uin, String userName) throws RemoteException {

    }

    @Override
    public void setForeground(int isForeground) throws RemoteException {

    }
}
