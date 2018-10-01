package cn.id0755.im.view.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.id0755.im.R;

public class MsgLeftVH extends RecyclerView.ViewHolder{
    public final static int RES_LAYOUT = R.layout.msg_list_item_left;

    public TextView getContent() {
        return content;
    }

    private TextView content;
    public MsgLeftVH(@NonNull View itemView) {
        super(itemView);
        content = itemView.findViewById(R.id.tv_content);
    }
}
