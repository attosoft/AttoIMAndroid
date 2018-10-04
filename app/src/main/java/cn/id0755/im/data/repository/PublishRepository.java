package cn.id0755.im.data.repository;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import cn.id0755.im.biz.GetPublishReq;
import cn.id0755.im.biz.GetPublishResp;
import cn.id0755.im.biz.SubjectReq;
import cn.id0755.im.biz.SubjectResp;
import cn.id0755.im.chat.proto.Push;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.sdk.android.biz.IRequestListener;
import cn.id0755.sdk.android.manager.MsgServiceManager;
import cn.id0755.sdk.android.service.push.IPushObserver;
import cn.id0755.sdk.android.utils.Log;

import static cn.id0755.im.chat.proto.Topic.TopicType.BROADCAST;
import static cn.id0755.im.chat.proto.Topic.TopicType.PERSON;

public class PublishRepository {
    private final static String TAG = PublishRepository.class.getSimpleName();

    public PublishRepository() {
        MsgServiceManager.getInstance().registerPushObserver(new IPushObserver() {
            @Override
            public void onReceive(Push.Message message) {
                if (message.getTopicType() == BROADCAST) {
                    TopicEntity topicEntity = new TopicEntity();
                    topicEntity.setTopicType(PERSON)
                            .setTopicId(message.getFrom());
                    mTopicOnlineChange.postValue(topicEntity);
                }
            }
        });
    }

    public MutableLiveData<List<TopicEntity>> getPushList() {
        MutableLiveData<List<TopicEntity>> mPublishList = new MutableLiveData<>();
        GetPublishReq req = new GetPublishReq();
        req.setListener(new IRequestListener<GetPublishResp>() {
            @Override
            public void onSuccess(GetPublishResp getPublishResp) {
                mPublishList.postValue(getPublishResp.getTopicEntities());
            }
        });
        MsgServiceManager.getInstance().send(req);
        return mPublishList;
    }

    public MutableLiveData<Boolean> subject(List<TopicEntity> topicEntities) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        SubjectReq subjectReq = new SubjectReq();
        subjectReq.setTopicEntities(topicEntities)
                .setListener(new IRequestListener<SubjectResp>() {
                    @Override
                    public void onSuccess(SubjectResp subjectResp) {
                        Log.w(TAG, "SubjectReq onSuccess:" + subjectResp.toString());
                        liveData.postValue(subjectResp != null);
                    }
                });
        MsgServiceManager.getInstance().send(subjectReq);
        return liveData;
    }

    private MutableLiveData<TopicEntity> mTopicOnlineChange = new MutableLiveData<>();

    public MutableLiveData<TopicEntity> subjectOnlineChange() {
        return mTopicOnlineChange;
    }


}
