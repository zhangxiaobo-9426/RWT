package com.example.rwt.ui.vicinity;

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
        holder.setText(R.id.vicinity_item_carcolor, vicinityCar.getCarcolor_url())
                .setText(R.id.vicinity_item_title, vicinityCar.getTitle())
                .setText(R.id.vicinity_item_distance, vicinityCar.getDistance())
                .setText(R.id.vicinity_item_time, vicinityCar.getTime())
                .setText(R.id.vicinity_item_location, vicinityCar.getLocation());
    }
}
