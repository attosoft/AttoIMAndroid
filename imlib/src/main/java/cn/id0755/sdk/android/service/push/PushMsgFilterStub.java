package cn.id0755.sdk.android.service.push;

import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import cn.id0755.im.IPushMessageFilter;
import cn.id0755.im.chat.proto.Push;
import cn.id0755.sdk.android.utils.Log;

public class PushMsgFilterStub extends IPushMessageFilter.Stub {
    private final static String TAG = PushMsgFilterStub.class.getSimpleName();

    @Override
    public boolean onReceive(int cmdId, byte[] buffer) throws RemoteException {
        try {
            Push.Message message = Push.Message.parseFrom(buffer);
            Log.w(TAG,"onReceive:"+message.getContent());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return false;
    }
}
