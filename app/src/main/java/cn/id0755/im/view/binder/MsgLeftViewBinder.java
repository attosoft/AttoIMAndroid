package cn.id0755.im.view.binder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cn.id0755.im.view.entity.MsgEntity;
import cn.id0755.im.view.holder.MsgLeftVH;
import me.drakeet.multitype.ItemViewBinder;

public class MsgLeftViewBinder extends ItemViewBinder<MsgEntity, MsgLeftVH> {
    @NonNull
    @Override
    protected MsgLeftVH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new MsgLeftVH(inflater.inflate(MsgLeftVH.RES_LAYOUT, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull MsgLeftVH holder, @NonNull MsgEntity item) {
        holder.getContent().setText(item.getContent());
    }
}
