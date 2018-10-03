package cn.id0755.im.biz;

import java.util.List;

import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.sdk.android.biz.BaseReq;

import static cn.id0755.im.biz.RequestType.SUBJECT;

public class SubjectReq extends BaseReq {
    private List<TopicEntity> topicEntities;

    public List<TopicEntity> getTopicEntities() {
        return topicEntities;
    }

    public SubjectReq setTopicEntities(List<TopicEntity> topicEntities) {
        this.topicEntities = topicEntities;
        return this;
    }

    @Override
    public int getReqType() {
        return SUBJECT;
    }
}
