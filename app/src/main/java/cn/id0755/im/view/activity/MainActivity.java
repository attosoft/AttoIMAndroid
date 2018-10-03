package cn.id0755.im.view.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.id0755.im.R;

import cn.id0755.im.utils.ToastUtil;
import cn.id0755.im.view.viewmodel.PublishListViewModel;
import cn.id0755.im.view.binder.DefaultTopicViewBinder;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.im.view.listener.ItemClickListener;
import me.drakeet.multitype.MultiTypeAdapter;

import static cn.id0755.im.view.activity.ChatActivity.KEY_TOPIC_ENTITY;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private RecyclerView mTopicList;
    private MultiTypeAdapter mTopicListAdapter = new MultiTypeAdapter();
    private List<TopicEntity> mTopicListData = new ArrayList<>();

    private PublishListViewModel mPublishListViewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("当前登陆：" + "");
        setContentView(R.layout.activity_main);
        mTopicList = findViewById(R.id.rv_topic_list);
        mTopicList.setLayoutManager(new LinearLayoutManager(this));
        mTopicList.setAdapter(mTopicListAdapter);
        mTopicListAdapter.register(TopicEntity.class, new DefaultTopicViewBinder(new ItemClickListener<TopicEntity>() {
            @Override
            public void onClick(int position, TopicEntity value) {
                Intent intent = new Intent();
                intent.putExtra(KEY_TOPIC_ENTITY, value);
                intent.setClass(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        }));
        mTopicListAdapter.setItems(mTopicListData);
        mTopicListAdapter.notifyDataSetChanged();
        initListeners();
        mPublishListViewModel = ViewModelProviders.of(this).get(PublishListViewModel.class);
        mPublishListViewModel.init();
        mPublishListViewModel.getPushList().observe(this, new Observer<List<TopicEntity>>() {
            @Override
            public void onChanged(@Nullable List<TopicEntity> topicEntities) {
                mTopicListData.clear();
                mTopicListData.addAll(topicEntities);
                mTopicListAdapter.notifyDataSetChanged();
                LiveData<Boolean> liveData = mPublishListViewModel.subjectTopic();
                liveData.observeForever(new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        ToastUtil.show(aBoolean?"订阅成功！":"订阅失败");
                    }
                });
            }
        });
    }

    private void initListeners() {

    }
}
