package com.example.rwt.ui.vicinity;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.rwt.R;
import com.example.rwt.ui.vicinity.entity.VicinityCar;

import org.jetbrains.annotations.NotNull;

public class VicinityAdapter extends BaseQuickAdapter<VicinityCar, BaseViewHolder> {
    public VicinityAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, VicinityCar vicinityCar) {
        //加载图片
        Glide.with(holder.itemView).load(vicinityCar.getCarcolor_url()).into((ImageView) holder.getView(R.id.vicinity_item_carcolor));
        holder.setText(R.id.vicinity_item_title, vicinityCar.getTitle())
                .setText(R.id.vicinity_item_distance, vicinityCar.getDistance())
                .setText(R.id.vicinity_item_time, vicinityCar.getTime())
                .setText(R.id.vicinity_item_location, vicinityCar.getLocation());
    }

}
