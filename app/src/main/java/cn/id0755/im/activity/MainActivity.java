package cn.id0755.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.id0755.im.R;
import cn.id0755.im.chat.proto.Topic;
import cn.id0755.sdk.android.task.ITaskListener;
import cn.id0755.im.task.SubjectTask;
import cn.id0755.im.view.binder.DefaultTopicViewBinder;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.im.view.listener.ItemClickListener;
import cn.id0755.sdk.android.manager.MessageServiceManager;
import cn.id0755.sdk.android.utils.Log;
import me.drakeet.multitype.MultiTypeAdapter;

import static cn.id0755.im.activity.ChatActivity.KEY_TOPIC_ENTITY;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private RecyclerView mTopicList;
    private MultiTypeAdapter mTopicListAdapter = new MultiTypeAdapter();
    private List<TopicEntity> mTopicListData = new ArrayList<>();

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
        mTopicListData.add(new TopicEntity());
        mTopicListData.add(new TopicEntity());
        mTopicListAdapter.notifyDataSetChanged();
        initListeners();

        SubjectTask subjectTask = new SubjectTask();
        subjectTask
                .setTopic("10086")
                .setListener(new ITaskListener<Topic.TopicResponse>() {
                    @Override
                    public void onResp(Topic.TopicResponse resp) {
                        Log.d(TAG, "onResp : " + resp.getErrMsg());
                    }

                    @Override
                    public void onTaskEnd(int errType, int errCode) {

                    }
                });
        MessageServiceManager.getInstance().send(subjectTask);
    }

    private void initListeners() {

    }
}
