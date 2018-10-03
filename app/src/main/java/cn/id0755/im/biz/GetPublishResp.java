package cn.id0755.im.biz;

import java.util.List;

import cn.id0755.im.view.entity.TopicEntity;

public class GetPublishResp {
    public List<TopicEntity> getTopicEntities() {
        return topicEntities;
    }

    public GetPublishResp setTopicEntities(List<TopicEntity> topicEntities) {
        this.topicEntities = topicEntities;
        return this;
    }

    private List<TopicEntity> topicEntities;
}
