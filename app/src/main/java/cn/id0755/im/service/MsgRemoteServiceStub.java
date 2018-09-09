package cn.id0755.im.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import cn.id0755.im.IMessageService;
import cn.id0755.im.IPushMessageFilter;
import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.entity.DeviceInfo;
import cn.id0755.im.manager.ConnectState;
import cn.id0755.im.manager.ConnectionManager;
import cn.id0755.im.manager.iinterface.IServerConnectionListener;
import cn.id0755.im.utils.RandomUtil;
import cn.id0755.sdk.android.utils.Log;

public class MsgRemoteServiceStub extends IMessageService.Stub {
    private final static String TAG = MsgRemoteServiceStub.class.getSimpleName();
    public static final String DEVICE_NAME = android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL;
    public static final String DEVICE_TYPE = "android-" + android.os.Build.VERSION.SDK_INT;
    private DeviceInfo info = new DeviceInfo(DEVICE_NAME, DEVICE_TYPE);
    private Context mContext;

    private Handler mHandler = new Handler();

    private IServerConnectionListener mConnectionListener = new IServerConnectionListener() {
        @Override
        public void onConnectState(ConnectState type) {

        }

        @Override
        public void onConnectStateChange(ConnectState type) {
            switch (type) {
                case CONNECTED:
                    break;
                case CONNECTING:
                    break;
                case DISCONNECT:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"IServerConnectionListener " + " DISCONNECT " + "开始重连");
                            ConnectionManager.getInstance().autoConnect(mConnectionListener);
                        }
                    }, RandomUtil.randInt(1, 5));
                    break;
                default:
                    break;
            }
        }
    };

    public MsgRemoteServiceStub(Context context) {
        mContext = context;
        ConnectionManager.getInstance().autoConnect(mConnectionListener);
    }

    @Override
    public int send(ITaskWrapper taskWrapper, Bundle taskProperties) throws RemoteException {
        return ConnectionManager.getInstance().send(taskWrapper) ? 1 : 0;
    }

    @Override
    public void cancel(int taskID) throws RemoteException {
        ConnectionManager.getInstance().cancel(taskID);
    }

    @Override
    public void registerPushMessageFilter(IPushMessageFilter filter) throws RemoteException {

    }

    @Override
    public void unregisterPushMessageFilter(IPushMessageFilter filter) throws RemoteException {

    }

    @Override
    public void setAccountInfo(long uin, String userName) throws RemoteException {

    }

    @Override
    public void setForeground(int isForeground) throws RemoteException {

    }
}
