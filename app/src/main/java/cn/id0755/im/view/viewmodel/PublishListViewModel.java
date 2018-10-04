package cn.id0755.im.view.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;


import javax.inject.Inject;

import cn.id0755.im.data.repository.PublishRepository;
import cn.id0755.im.inject.component.DaggerPublishListComponent;
import cn.id0755.im.view.entity.TopicEntity;

public class PublishListViewModel extends ViewModel {
    private MutableLiveData<List<TopicEntity>> mPublishListLiveData = null;
    @Inject
    protected PublishRepository mPublishRepository = null;

    public PublishListViewModel() {
        DaggerPublishListComponent.create().inject(this);
    }

    /**
     * 获取发布者列表，包含所有在线的人
     *
     * @return
     */
    public MutableLiveData<List<TopicEntity>> getPushList() {
        if (mPublishListLiveData == null) {
            mPublishListLiveData = mPublishRepository.getPushList();
        }
        return mPublishRepository.getPushList();
    }

    /**
     * 订阅主题
     *
     * @return
     */
    public MutableLiveData<Boolean> subjectTopic() {
        return mPublishRepository.subject(mPublishListLiveData.getValue());
    }

    public MutableLiveData<TopicEntity> subjectOnlineChange(){
        return mPublishRepository.subjectOnlineChange();
    }

    public void init() {

    }
}
