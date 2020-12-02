package com.example.rwt.ui.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.rwt.R;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {
    /* 自己的经纬度坐标*/
    protected LatLng myPos = new LatLng(26.40846,106.632693);
    protected LatLng IconPos = new LatLng(26.408609,106.633615);
    //获取地图和地图控件
    protected MapView base_mapview;
    protected BaiduMap baiduMap =null;
    private LocationClient locationClient;
    //是否是第一次获取位置信息
    private boolean isFirstLocation =true;
    private ImageView base_zoom_in,base_zoom_out,base_location_my;

    //显示覆盖物的文字
    private TextView tv_title;
    private View pop;
    protected PoiInfo poiInfo;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationClient = new LocationClient(getContext());
        locationClient.registerLocationListener(new MyLocationListener());

        base_mapview = view.findViewById(R.id.base_mapview);
//       //隐藏比例尺按钮
//       mapView.showScaleControl(false);
        //隐藏缩放按钮
        base_mapview.showZoomControls(false);
        //设置比例尺位置
//        mapView.setScaleControlPosition(new Point(0,0));
        //设置缩放位置
//        mapView.setZoomControlsPosition(new Point(0,0));

        baiduMap = base_mapview.getMap();

        //显示的地图样式
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置是否显示交通图
        baiduMap.setTrafficEnabled(true);
//       //设置是否显示热力图
//       baiduMap.setBaiduHeatMapEnabled(true);

        //可以定位自己的位置
        baiduMap.setMyLocationEnabled(true);

        /*-----------------缩小------------------------*/
        base_zoom_in = view.findViewById(R.id.base_zoom_in);
        base_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZoomIn(view);
            }
        });

        /*-----------------放大------------------------*/
        base_zoom_out= view.findViewById(R.id.base_zoom_out);
        base_zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZoomOut(view);
            }
        });


        //动态申请权限
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

    }

    public void showToast(CharSequence text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    //判断用户是否同意全部请求的权限
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


    //初始化定位
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

        option.setIgnoreKillProcess(true);
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
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        option.setIsNeedLocationPoiList(true);
        //可选，是否需要周边POI信息，默认为不需要，即参数为false
        //如果开发者需要获得周边POI信息，此处必须为true

        locationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

    }

    private class MyLocationListener extends BDAbstractLocationListener {
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

    //显示自己的当前位置
    private void navigateTo(BDLocation bdLocation){
        if (isFirstLocation) {
            //获取纬经度（先设置纬度，在是经度）
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            //更新地图
//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);

            //获取自己的设置的经纬度坐标
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(myPos);


            baiduMap.animateMapStatus(update);

            //设置大小
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocation =false;
        }

        //标记自己位置的图标
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.longitude(myPos.longitude);
        locationBuilder.latitude(myPos.latitude);
//        locationBuilder.longitude(bdLocation.getLongitude());
//        locationBuilder.latitude(bdLocation.getLatitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);



        //标志建筑物点击事件
        baiduMap.setOnMarkerClickListener(onMarkerClickListener);
        //标志建筑物拖动事件
        baiduMap.setOnMarkerDragListener(onMarkerDragListener);


    }
    //标志拖动监听器
    BaiduMap.OnMarkerDragListener  onMarkerDragListener = new BaiduMap.OnMarkerDragListener() {
        //标志正在拖动
        @Override
        public void onMarkerDrag(Marker marker) {
            base_mapview.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
        //标志拖动结束
        @Override
        public void onMarkerDragEnd(Marker marker) {
            base_mapview.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
        //标志开始拖动
        @Override
        public void onMarkerDragStart(Marker marker) {
            base_mapview.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
    };
    BaiduMap.OnMarkerClickListener onMarkerClickListener =new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //显示一个停车场的信息 vicinity_pop
            if (pop == null){
                pop =View.inflate(getActivity(), R.layout.vicinity_pop,null);
                tv_title = pop.findViewById(R.id.tv_vicinity_location_title);

                base_mapview.addView(pop,createLayoutParams(marker.getPosition()));
            }else {
                base_mapview.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
            }


            tv_title.setText(poiInfo.name);


            return true;
        }
    };

    /**
     * 创建一个布局参数
     * @param position（位置）
     * @return
     */
    private MapViewLayoutParams createLayoutParams(LatLng position){
        MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder();
        builder.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode); //指定坐标类型为经纬度
        builder.position(position);  //设置标志位置
        builder.yOffset(-65);
        MapViewLayoutParams params = builder.build();
        return params;
    }

    /**
     * 放大地图缩放级别
     *
     * @param v
     */
    public void setZoomIn(View v) {
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
    }

    /**
     * 缩小地图缩放级别
     *
     * @param v
     */
    public void setZoomOut(View v) {
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        locationClient.stop();
//        baiduMap.setMyLocationEnabled(false);
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
}
