package cn.id0755.im.task;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.UUID;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.im.chat.proto.Topic;
import cn.id0755.sdk.android.task.ITaskListener;
import cn.id0755.sdk.android.utils.MessageUtil;

public class SubjectTask extends ITaskWrapper.Stub implements ITaskWrapper{
    private ITaskListener<Topic.TopicResponse> listener = null;

    public SubjectTask setListener(ITaskListener<Topic.TopicResponse> listener) {
        this.listener = listener;
        return this;
    }

    public SubjectTask setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    private String topic = null;

    @Override
    public Bundle getProperties() throws RemoteException {
        return null;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        Topic.TopicRequest topicRequest = Topic.TopicRequest
                .newBuilder()
                .setTopic(topic)
                .setTopicType(Topic.TopicType.PERSON)
                .build();
        UUID uuid = UUID.randomUUID();
        return MessageUtil.wrap(Message.CMD_ID.SUBJECT_TOPIC_REQ, uuid.toString(), topicRequest).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        Topic.TopicResponse response = null;
        try {
            response = Topic.TopicResponse.getDefaultInstance().getParserForType().parseFrom(buf, 0, buf.length);
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

    }
}
