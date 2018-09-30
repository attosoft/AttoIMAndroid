package cn.id0755.im.task;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.UUID;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.utils.MessageUtil;

public class LoginTask extends ITaskWrapper.Stub implements ITaskWrapper {
    /*  */
    private Bundle mProperties = new Bundle();

    private String account;
    private String password;

    public LoginTask(String account, String password) {
        this.account = account;
        this.password = password;
    }

    private ITaskListener<Login.LoginResponse> listener = null;

    public LoginTask setListener(ITaskListener<Login.LoginResponse> listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Bundle getProperties() throws RemoteException {
        return mProperties;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        Login.LoginRequest loginRequest = Login.LoginRequest.newBuilder()
                .setAccount(account)
                .setPassword(password)
                .build();
        UUID uuid = UUID.randomUUID();
        return MessageUtil.wrap(Message.CMD_ID.LOGIN_REQ, uuid.toString(), loginRequest).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        Login.LoginResponse response = null;
        try {
            response = Login.LoginResponse.getDefaultInstance().getParserForType().parseFrom(buf, 0, buf.length);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (response != null && listener != null) {
            listener.onResp(response);
        }
        return response != null ? 1 : 0;
    }

    @Override
    public void onTaskEnd(int errType, int errCode) throws RemoteException {
        if (listener != null){
            listener.onTaskEnd(errType, errCode);
        }
    }

}
