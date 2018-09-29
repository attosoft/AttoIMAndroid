package cn.id0755.sdk.android.task;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.utils.MessageUtil;

public class LoginTask extends ITaskWrapper.Stub implements ITaskWrapper {
    private Bundle mProperties = new Bundle();
    public LoginTask() {

    }

    @Override
    public Bundle getProperties() throws RemoteException {
        return mProperties;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        Login.LoginRequest loginRequest = Login.LoginRequest.newBuilder()
                .setAccount("13510773000")
                .setPassword("123456")
                .build();
        return MessageUtil.wrap(Message.CMD_ID.LOGIN_REQ,loginRequest).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        return 0;
    }

    @Override
    public void onTaskEnd(int errType, int errCode) throws RemoteException {

    }

}
