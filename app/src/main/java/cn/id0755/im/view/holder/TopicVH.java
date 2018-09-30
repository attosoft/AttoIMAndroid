package cn.id0755.im.view.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.id0755.im.R;

public class TopicVH extends RecyclerView.ViewHolder {
    public final static int RES_LAYOUT = R.layout.topic_list_item;

    public View getContainer() {
        return container;
    }

    public TextView getTopicName() {
        return topicName;
    }

    public TextView getLastMsg() {
        return lastMsg;
    }

    private View container;
    private TextView topicName;
    private TextView lastMsg;

    public TopicVH(@NonNull View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.cl_contain);
        topicName = itemView.findViewById(R.id.tv_topic_name);
        lastMsg = itemView.findViewById(R.id.tv_last_msg);
    }
}
