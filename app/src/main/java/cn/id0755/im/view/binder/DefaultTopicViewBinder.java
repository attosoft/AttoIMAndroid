package cn.id0755.im.view.binder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.id0755.im.R;
import cn.id0755.im.view.entity.TopicEntity;
import cn.id0755.im.view.holder.TopicVH;
import cn.id0755.im.view.listener.ItemClickListener;
import me.drakeet.multitype.ItemViewBinder;

public class DefaultTopicViewBinder extends ItemViewBinder<TopicEntity, TopicVH> {
    private ItemClickListener onClickListener;

    public DefaultTopicViewBinder(ItemClickListener listener) {
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    protected TopicVH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new TopicVH(inflater.inflate(TopicVH.RES_LAYOUT, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull TopicVH holder, @NonNull TopicEntity item) {
        if (holder.getAdapterPosition() % 2 == 0) {
            holder.getContainer().setBackgroundColor(holder.getContainer().getResources().getColor(R.color.colorAccent));
            holder.getContainer().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(holder.getAdapterPosition(), item);
                    }
                }
            });
        }
    }
}
