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
        return MessageUtil.wrap(Message.CMD_ID.LOGIN_REQ, uuid.toString(),loginRequest).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        int success = 1;
        try {
            Login.LoginResponse response = Login.LoginResponse.getDefaultInstance().getParserForType().parseFrom(buf, 0, buf.length);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            success = 0;
        }
        return success;
    }

    @Override
    public void onTaskEnd(int errType, int errCode) throws RemoteException {

    }

}
