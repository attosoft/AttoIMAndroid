package cn.id0755.im.biz;

import cn.id0755.im.chat.proto.Topic;
import cn.id0755.sdk.android.biz.BaseReq;

public class TextMsgReq extends BaseReq {
    private String content;
    private String from;
    private String to;
    private Topic.TopicType topicType;
    private String topicId;

    public String getFrom() {
        return from;
    }

    public TextMsgReq setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public TextMsgReq setTo(String to) {
        this.to = to;
        return this;
    }

    public Topic.TopicType getTopicType() {
        return topicType;
    }

    public TextMsgReq setTopicType(Topic.TopicType topicType) {
        this.topicType = topicType;
        return this;
    }

    public String getTopicId() {
        return topicId;
    }

    public TextMsgReq setTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public TextMsgReq setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public int getReqType() {
        return RequestType.TEXT_MSG;
    }
}
