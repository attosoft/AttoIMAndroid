package cn.id0755.sdk.android.service.push;

import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Set;

import cn.id0755.im.IPushMessageFilter;
import cn.id0755.im.chat.proto.Push;
import cn.id0755.sdk.android.utils.Log;
import io.netty.util.internal.ConcurrentSet;

public class PushMsgFilterStub extends IPushMessageFilter.Stub implements IPushMessageFilter{
    private final static String TAG = PushMsgFilterStub.class.getSimpleName();
    private Set<IPushObserver> observers = new ConcurrentSet<>();

    public void register(IPushObserver observer){
        observers.add(observer);
    }

    public void unRegister(IPushObserver observer){
        observers.remove(observer);
    }

    @Override
    public boolean onReceive(int cmdId, byte[] buffer) throws RemoteException {
        try {
            Push.Message message = Push.Message.parseFrom(buffer);
            Log.w(TAG,"onReceive:"+message.getContent());
            for (IPushObserver observer: observers){
                observer.onReceive(message);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return false;
    }
}
