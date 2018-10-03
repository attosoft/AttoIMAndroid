package cn.id0755.im.view.entity;

import java.io.Serializable;

import cn.id0755.im.chat.proto.Topic;

public class TopicEntity implements Serializable {
    private Topic.TopicType topicType;
    private String topicId;

    public Topic.TopicType getTopicType() {
        return topicType;
    }

    public TopicEntity setTopicType(Topic.TopicType topicType) {
        this.topicType = topicType;
        return this;
    }

    public String getTopicId() {
        return topicId;
    }

    public TopicEntity setTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

}
