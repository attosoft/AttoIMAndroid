package cn.id0755.sdk.android.task;

import android.os.Bundle;
import android.os.RemoteException;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.HeartBeat;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.utils.MessageUtil;

public class PingTask extends ITaskWrapper.Stub implements ITaskWrapper {
    private Bundle mProperties = new Bundle();

    public PingTask() {

    }

    @Override
    public Bundle getProperties() throws RemoteException {
        return mProperties;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        HeartBeat.Ping ping = HeartBeat.Ping
                .newBuilder()
                .setCmdId(Message.CMD_ID.PING)
                .build();
        return MessageUtil.wrap(Message.CMD_ID.PING,ping).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        return 0;
    }

    @Override
    public void onTaskEnd(int errType, int errCode) throws RemoteException {

    }
}
