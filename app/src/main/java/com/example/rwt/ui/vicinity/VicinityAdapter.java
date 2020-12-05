package com.example.rwt.ui.vicinity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    protected void setOnItemClick(@NotNull View v, int position) {
        super.setOnItemClick(v, position);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, VicinityCar vicinityCar) {
        //加载图片
        Glide.with(holder.itemView).load(vicinityCar.getCarcolor_url()).into((ImageView) holder.getView(R.id.vicinity_item_carcolor));
        holder.setText(R.id.vicinity_item_title, vicinityCar.getTitle())
                .setText(R.id.vicinity_item_distance, vicinityCar.getDistance())
                .setText(R.id.vicinity_item_time, vicinityCar.getTime())
                .setText(R.id.vicinity_item_location, vicinityCar.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("VICINITY_TITLE",vicinityCar.getTitle());
                bundle.putDouble("VICINITY_LATITUDE",vicinityCar.getLatitude());
                bundle.putDouble("VICINITY_LONGITUDE",vicinityCar.getLongitude());
                bundle.putString("VICINITY_TIME",vicinityCar.getTime());
                bundle.putString("VICINITY_LOCATION",vicinityCar.getLocation());
                controller.navigate(R.id.action_vicinityFragment_to_searchDetailsFragment,bundle);
            }
        });

    }


}
