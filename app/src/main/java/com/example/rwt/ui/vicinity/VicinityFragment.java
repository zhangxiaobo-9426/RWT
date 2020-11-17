package com.example.rwt.ui.vicinity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.rwt.R;
import com.example.rwt.ui.network.ApiDemo;
import com.example.rwt.ui.network.RetrofitFactory;
import com.example.rwt.ui.vicinity.entity.VicinityCar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VicinityFragment extends Fragment implements OnGetPoiSearchResultListener {
    private View view;
    //用于获取自己的信息
    private TextView mLocationInfo;

    //获取地图和地图控件
    private MapView mapView;
    private BaiduMap baiduMap =null;
    private LocationClient locationClient;
    //是否是第一次获取位置信息
    private boolean isFirstLocation =true;
    private ImageView zoom_in,zoom_out;


    private VicinityViewModel mViewModel;

    //显示覆盖物的文字
    private TextView tv_title;
    private View pop;

    /* 自己的经纬度坐标*/
    protected LatLng myPos = new LatLng(26.40846,106.632693);
    protected LatLng IconPos = new LatLng(26.408609,106.633615);

    // 声明 PoiSearch
    private PoiSearch mPoiSearch = null;
    private PoiInfo poiInfo;

    //用于显示列表
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
        mViewModel = ViewModelProviders.of(this).get(VicinityViewModel.class);
        // TODO: Use the ViewModel


        /*-----------------缩小------------------------*/
        zoom_in = view.findViewById(R.id.zoom_in);
        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZoomIn(view);
            }
        });


        /*-----------------放大------------------------*/
        zoom_out= view.findViewById(R.id.zoom_out);
        zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZoomOut(view);
            }
        });

        /*-----------------定位------------------------*/
       mLocationInfo =view.findViewById(R.id.mLocationInfo);
       locationClient = new LocationClient(getContext());
       locationClient.registerLocationListener(new MyLocationListener());

       mapView = view.findViewById(R.id.bmapView);
//       //隐藏比例尺按钮
//       mapView.showScaleControl(false);
       //隐藏缩放按钮
        mapView.showZoomControls(false);
        //设置比例尺位置
//        mapView.setScaleControlPosition(new Point(0,0));
        //设置缩放位置
//        mapView.setZoomControlsPosition(new Point(0,0));

       baiduMap = mapView.getMap();

       //显示的地图样式
       baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
       //设置是否显示交通图
       baiduMap.setTrafficEnabled(true);
//       //设置是否显示热力图
//       baiduMap.setBaiduHeatMapEnabled(true);

       //可以定位自己的位置
        baiduMap.setMyLocationEnabled(true);

        /*-----------------Poi搜索------------------------*/
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        /*-----------------Poi城市搜索------------------------*/
//        mPoiSearch.searchInCity(new PoiCitySearchOption()
//                .city("贵阳") //必填
//                .keyword("停车场") //必填
//                .pageNum(10));
        /*
         * 设置矩形检索区域
         *   西南 106.630983,26.405209
         *   东北 106.636043,26.410937
         *
         * 106.623059,26.395228
         * 106.64584,26.418656
         */
        /*-----------------Poi周边搜索------------------------*/
        LatLngBounds searchBounds = new LatLngBounds.Builder()
                .include(new LatLng( 26.395228, 106.623059))
                .include(new LatLng( 26.418656, 106.64584))
                .build();

        /*
         * 在searchBounds区域内检索停车场
         */
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchBounds)
                .keyword("停车场"));



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
    //网络加载
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
    //本地加载
//    private void loadData(){
//        List<VicinityCar> list = new ArrayList<>();
//        /*
//         *     private int id;
//         *     private String carcolor_url;
//         *     private String title;
//         *     private String distance;
//         *     private String time;
//         *     private String location;
//         * **/
//        list.add(new VicinityCar(1,"h","贵安新区","425米","3分钟","思雅路"));
//        list.add(new VicinityCar(2,"t","贵安新区","425米","3分钟","思雅路"));
//        list.add(new VicinityCar(3,"t","贵安新区","425米","3分钟","思雅路"));
//        list.add(new VicinityCar(4,"p","贵安新区","425米","3分钟","思雅路"));
//        list.add(new VicinityCar(5,"s","贵安新区","425米","3分钟","思雅路"));
//        list.add(new VicinityCar(6,"s","贵安新区","425米","3分钟","思雅路"));
//
//        adapter.setNewInstance(list);
//    }

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


//        //文字覆盖物
//        TextOptions options=new TextOptions();
//        options.position(myPos).text("我的位置").fontSize(20).fontColor(0X55FF0000);
//        baiduMap.addOverlay(options);

//       initMarker();

        //标志建筑物点击事件
       baiduMap.setOnMarkerClickListener(onMarkerClickListener);
        //标志建筑物拖动事件
       baiduMap.setOnMarkerDragListener(onMarkerDragListener);


//        poiSearch = PoiSearch.newInstance();
//        poiSearch.setOnGetPoiSearchResultListener(this);
//
//        poiOverlay = new PoiOverlay(baiduMap){
//            @Override
//            public boolean onPoiClick(int i) {
//                PoiInfo poiInfo=getPoiResult().getAllPoi().get(i);
//                Toast.makeText(getContext(),poiInfo.name + ","+ poiInfo.address, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        };
//        baiduMap.setOnMarkerClickListener(poiOverlay);
//        // poiSearch.searchInBound(getSearchInBoundParams());
//        poiSearch.searchInCity(getSearchInCityParams());

    }
//    //城市搜索
//    private PoiCitySearchOption getSearchInCityParams(){
//        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
//        poiCitySearchOption.city("贵阳市").keyword("加油站").pageNum(4);
//        return poiCitySearchOption;
//    }
//    //周边搜索
//    private PoiBoundSearchOption getSearchInBoundParams(){
//        PoiBoundSearchOption poiBoundSearchOption =new PoiBoundSearchOption();
//        /*106.630983,26.405209
//        106.636043,26.410937
//         * 西南 106.631844,26.406081
//         * 东北 106.63497,26.409575
//         * 两个点之间围成的矩形
//         */
//        LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                .include(new LatLng(26.405209,106.630983))
//                .include(new LatLng(26.410937,106.636043)).build();
//
//        poiBoundSearchOption.bound(latLngBounds); // 指定搜索范围
//        poiBoundSearchOption.keyword("加油站");  //指定搜索内容
//        return poiBoundSearchOption;
//    }
    //获取兴趣点信息
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
//        不等于没有错误，就是有错误（后期可以更详细判断错误类型）
//        if (poiResult == null && poiResult.error != SearchResult.ERRORNO.NO_ERROR){
//            Toast.makeText(getContext(), "没有检索到结果", Toast.LENGTH_SHORT).show();
//            return;
//        }
//            //创建PoiOverlay对象
//            PoiOverlay poiOverlay = new PoiOverlay(baiduMap);
//
//            //设置Poi检索数据
//            poiOverlay.setData(poiResult);
//
//            //将poiOverlay添加至地图并缩放至合适级别
//            poiOverlay.addToMap();
//            poiOverlay.zoomToSpan();

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            baiduMap.clear();

            //创建PoiOverlay对象
            PoiOverlay poiOverlay = new PoiOverlay(baiduMap){
                @Override
                public boolean onPoiClick(int i) {
                    poiInfo = getPoiResult().getAllPoi().get(i);
//                    Toast.makeText(getContext(), poiInfo.name+" , "+poiInfo.address, Toast.LENGTH_SHORT).show();
                    return true;
                }
            };
            baiduMap.setOnMarkerClickListener(poiOverlay);

            //设置Poi检索数据
            poiOverlay.setData(poiResult);

            //将poiOverlay添加至地图并缩放至合适级别
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();
        }


    }
    //获取兴趣点详情信息0
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }




    //标志拖动监听器
    BaiduMap.OnMarkerDragListener  onMarkerDragListener = new BaiduMap.OnMarkerDragListener() {
        //标志正在拖动
        @Override
        public void onMarkerDrag(Marker marker) {
            mapView.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
        //标志拖动结束
        @Override
        public void onMarkerDragEnd(Marker marker) {
            mapView.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
        //标志开始拖动
        @Override
        public void onMarkerDragStart(Marker marker) {
            mapView.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
        }
    };
    BaiduMap.OnMarkerClickListener onMarkerClickListener =new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //显示一个停车场的信息 vicinity_pop
            if (pop == null){
                pop =View.inflate(getActivity(), R.layout.vicinity_pop,null);
                tv_title = pop.findViewById(R.id.tv_vicinity_location_title);

                mapView.addView(pop,createLayoutParams(marker.getPosition()));
            }else {
                mapView.updateViewLayout(pop,createLayoutParams(marker.getPosition()));
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
     * 自定义标志
     */
//   private void initMarker(){
//       MarkerOptions options1 = new MarkerOptions();
//       BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.vicinity_location_blue);
//       options1.position(IconPos).icon(bitmapDescriptor).draggable(true).title("中间");
//       baiduMap.addOverlay(options1);
//       options1.position(new LatLng(IconPos.latitude + 0.001,IconPos.longitude)).title("上面").icon(bitmapDescriptor).draggable(true);
//       baiduMap.addOverlay(options1);
//       options1.position(new LatLng(IconPos.latitude - 0.001,IconPos.longitude)).title("下面").icon(bitmapDescriptor).draggable(true);
//       baiduMap.addOverlay(options1);
//   }
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
