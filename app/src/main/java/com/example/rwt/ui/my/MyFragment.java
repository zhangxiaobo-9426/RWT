package com.example.rwt.ui.my;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rwt.MainActivity;
import com.example.rwt.R;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {

    private MyViewModel mViewModel;
    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // TODO: Use the ViewModel
        initView();

        final ImageView imageView = getView().findViewById(R.id.img_setting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_myFragment_to_settingFragment);
            }
        });
    }
    public void initView(){
        Banner banner = null;
        banner = getView().findViewById(R.id.my_banner);
        //放图片地址的集合
        List<Integer> list_path = new ArrayList<>();
        list_path.add(R.drawable.my_advertise);
        list_path.add(R.drawable.my_advertise);

        banner.setImageLoader(new MainActivity.ImageLoadBanner());   //设置图片加载器
        banner.setImages(list_path);  //设置banner中显示图片
        //设置轮播间隔时间
        banner.setDelayTime(3000).isAutoPlay(true).start();
        //设置是否为自动轮播，默认是“是”。
        //设置完毕后调用
    }
}
