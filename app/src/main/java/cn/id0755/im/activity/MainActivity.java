package cn.id0755.im.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.id0755.im.R;
import cn.id0755.im.view.binder.DefaultTopicViewBinder;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.im.view.listener.ItemClickListener;
import me.drakeet.multitype.MultiTypeAdapter;

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

            }
        }));
        mTopicListAdapter.setItems(mTopicListData);
        mTopicListData.add(new TopicEntity());
        mTopicListData.add(new TopicEntity());
        mTopicListAdapter.notifyDataSetChanged();
        initListeners();
    }

    private void initListeners() {

    }
}
