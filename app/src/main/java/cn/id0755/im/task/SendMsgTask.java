package cn.id0755.im.task;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.UUID;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Chat;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.utils.MessageUtil;

public class SendMsgTask extends ITaskWrapper.Stub implements ITaskWrapper {
    @Override
    public Bundle getProperties() throws RemoteException {
        return null;
    }
    private ITaskListener<Chat.SendMessageResponse> listener = null;

    public SendMsgTask setListener(ITaskListener<Chat.SendMessageResponse> listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        UUID uuid = UUID.randomUUID();
        Chat.SendMessageRequest sendMessageRequest = Chat.SendMessageRequest.newBuilder()
                .setAccessToken("")
                .setFrom("10086")
                .setText("hello world")
                .setTo("10000")
                .setTopic("")
                .build();
        return MessageUtil.wrap(Message.CMD_ID.LOGIN_REQ, uuid.toString(), sendMessageRequest).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        Chat.SendMessageResponse response = null;
        try {
            response = Chat.SendMessageResponse.getDefaultInstance().getParserForType().parseFrom(buf, 0, buf.length);
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
