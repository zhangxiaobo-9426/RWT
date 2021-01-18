package com.example.rwt.ui.vicinity.navigation;


import android.content.Context;
import android.os.Bundle;

import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.impl.BaiduNaviManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rwt.R;
import com.example.rwt.ui.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends BaseFragment {
    private Context context;
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "lbstest";    //app在SD卡中的目录名
    String authinfo = null;
    private boolean hasInitSuccess = false;                        //初始化标志位

    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    /**
     * 使用SDK前，先进行百度服务授权和引擎初始化
     */
    private void initNavi() {


        BaiduNaviManagerFactory.getBaiduNaviManager().init(context,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(), authinfo, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(getActivity().getApplicationContext(), "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(getActivity().getApplicationContext(), "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;

                        // 初始化tts
//                        initTTS();
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Toast.makeText(getActivity().getApplicationContext(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }

                });
    }
    }
