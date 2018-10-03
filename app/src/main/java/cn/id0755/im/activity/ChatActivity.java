package cn.id0755.im.activity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.id0755.im.IPushMessageFilter;
import cn.id0755.im.R;
import cn.id0755.im.biz.RequestType;
import cn.id0755.im.biz.TextMsgReq;
import cn.id0755.im.biz.TextMsgResp;
import cn.id0755.im.chat.proto.Chat;
import cn.id0755.im.chat.proto.Topic;
import cn.id0755.sdk.android.biz.IRequestListener;
import cn.id0755.sdk.android.service.push.PushMsgFilterStub;
import cn.id0755.sdk.android.task.CommonTask;
import cn.id0755.sdk.android.task.ITaskListener;
import cn.id0755.im.task.SendMsgTask;
import cn.id0755.im.view.binder.MsgLeftViewBinder;
import cn.id0755.im.view.binder.MsgRightViewBinder;
import cn.id0755.im.view.entity.MsgEntity;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.sdk.android.manager.MessageServiceManager;
import cn.id0755.sdk.android.utils.Log;
import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = ChatActivity.class.getSimpleName();

    public final static String KEY_TOPIC_ENTITY = "key_entity";

    private EditText mEditInput;
    private Button mSend;
    private RecyclerView mMsgList;
    private MultiTypeAdapter mMsgListAdapter = new MultiTypeAdapter();
    private List<MsgEntity> mData = new ArrayList<>();

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send:
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setContent(mEditInput.getText().toString());
                    msgEntity.setLeft(false);
                    mData.add(msgEntity);
                    mMsgListAdapter.setItems(mData);
                    mMsgListAdapter.notifyDataSetChanged();
                    TextMsgReq req = new TextMsgReq();
                    req.setContent(mEditInput.getText().toString())
                            .setFrom("10086")
                            .setTo("10086")
                            .setTopicType(Topic.TopicType.PERSON)
                            .setListener(new IRequestListener<TextMsgResp>() {
                                @Override
                                public void onSuccess(TextMsgResp textMsgResp) {
                                    Log.d(TAG, "onSuccess " + textMsgResp.getErrorMsg());
                                }
                            });
                    MessageServiceManager.getInstance().send(req);

                    mEditInput.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TopicEntity topicEntity = (TopicEntity) getIntent().getSerializableExtra(KEY_TOPIC_ENTITY);

        mEditInput = findViewById(R.id.input_text);
        mEditInput.addTextChangedListener(mTextWatcher);
        mSend = findViewById(R.id.send);
        mSend.setOnClickListener(mOnClickListener);

        mMsgList = findViewById(R.id.rv_msg_list);
        mMsgList.setLayoutManager(new LinearLayoutManager(this));
        mMsgList.setAdapter(mMsgListAdapter);
        mMsgListAdapter.register(MsgEntity.class)
                .to(new MsgLeftViewBinder(), new MsgRightViewBinder())
                .withClassLinker(new ClassLinker<MsgEntity>() {
                    @NonNull
                    @Override
                    public Class<? extends ItemViewBinder<MsgEntity, ?>> index(int position, @NonNull MsgEntity msgEntity) {
                        return msgEntity.isLeft() ? MsgLeftViewBinder.class : MsgRightViewBinder.class;
                    }
                });
        mMsgListAdapter.setItems(mData);
        mMsgListAdapter.notifyDataSetChanged();
    }


}
