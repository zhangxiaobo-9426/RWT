package com.example.rwt.ui.vicinity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.rwt.R;
import com.example.rwt.ui.network.ApiDemo;
import com.example.rwt.ui.network.RetrofitFactory;
import com.example.rwt.ui.vicinity.entity.VicinityCar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VicinityFragment extends Fragment {

    private VicinityViewModel mViewModel;
    private TextView mLocationInfo;
    private LocationClient locationClient;
    private View view;
    private MapView mapView;
    private BaiduMap baiduMap =null;
    private boolean isFirstLocation =true;

    private RecyclerView recyclerView;
    private VicinityAdapter adapter;

    public static VicinityFragment newInstance() {
        return new VicinityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.vicinity_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        SDKInitializer.initialize(getActivity().getApplicationContext());

        mViewModel = ViewModelProviders.of(this).get(VicinityViewModel.class);
        // TODO: Use the ViewModel

       mLocationInfo =view.findViewById(R.id.mLocationInfo);
       locationClient = new LocationClient(getContext());
       locationClient.registerLocationListener(new MyLocationListener());

       mapView = view.findViewById(R.id.bmapView);
       baiduMap = mapView.getMap();

       //显示的地图样式
       baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

       //可以定位自己的位置
        baiduMap.setMyLocationEnabled(true);



        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String [] permissions =permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }else {
            requestLocation();
        }


        recyclerView = view.findViewById(R.id.recyclerView);
        // 第一步：指定布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        // 第二步：设置适配器
        adapter = new VicinityAdapter(R.layout.vicinity_item);
        recyclerView.setAdapter(adapter);
        //recyclerView分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

//        loadData();
        loadDatahttp();

    }

    private void loadDatahttp(){
        // 获取Retrofit对象
        RetrofitFactory.getRetrofit().create(ApiDemo.class)
                .getCar()
                // 切换到IO现场执行网络请求
                .subscribeOn(Schedulers.io())
                // 切换到UI线程执行UI操作
                .observeOn(AndroidSchedulers.mainThread())
                // 获取网络请求结果
                .subscribe(new Consumer<List<VicinityCar>>()
                           {
                               @Override
                               public void accept(List<VicinityCar> repos) throws Exception
                               {

                                   adapter.addData(repos);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception
                            {
                                throwable.printStackTrace();
                            }
                        });
    }
    private void loadData(){
        List<VicinityCar> list = new ArrayList<>();
        /**
         *     private int id;
         *     private String carcolor_url;
         *     private String title;
         *     private String distance;
         *     private String time;
         *     private String location;
         * **/
        list.add(new VicinityCar(1,"h","贵安新区","425米","3分钟","思雅路"));
        list.add(new VicinityCar(2,"t","贵安新区","425米","3分钟","思雅路"));
        list.add(new VicinityCar(3,"t","贵安新区","425米","3分钟","思雅路"));
        list.add(new VicinityCar(4,"p","贵安新区","425米","3分钟","思雅路"));
        list.add(new VicinityCar(5,"s","贵安新区","425米","3分钟","思雅路"));
        list.add(new VicinityCar(6,"s","贵安新区","425米","3分钟","思雅路"));

        adapter.setNewInstance(list);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getContext(), "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
        }
    }

    private void requestLocation(){
        initLocation();
        locationClient.start();
    }
    private void initLocation(){
        LocationClientOption option =new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

                option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

                option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

                option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

                option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

                option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

                option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

                option.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

                option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

                option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

                option.setIsNeedAddress(true);

                locationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

    }
    private class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            navigateTo(bdLocation);
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("维度：").append(bdLocation.getLatitude()).append("\n");
//            currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
//            currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
//            currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
//            currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
//            currentPosition.append("村镇：").append(bdLocation.getTown()).append("\n");
//            currentPosition.append("村道：").append(bdLocation.getStreet()).append("\n");
//            currentPosition.append("地址：").append(bdLocation.getAddrStr()).append("\n");
//            currentPosition.append("定位方式：");
//            if (bdLocation.getLocType() == BDLocation.TypeCacheLocation){
//                currentPosition.append("GPS");
//            }else  if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
//                currentPosition.append("网络");
//            }
//            mLocationInfo.setText(currentPosition);


        }
    }
    private void navigateTo(BDLocation bdLocation){
        if (isFirstLocation) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);

            //设置大小
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocation =false;
        }

        //显示自己位置的图标
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.longitude(bdLocation.getLongitude());
        locationBuilder.latitude(bdLocation.getLatitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        locationClient.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
