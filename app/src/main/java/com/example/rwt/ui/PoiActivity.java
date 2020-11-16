package com.example.rwt.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.rwt.R;

public class PoiActivity extends AppCompatActivity implements OnGetPoiSearchResultListener {
    private PoiSearch mPoiSearch = null;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        mMapView = findViewById(R.id.test_mapView);
        mBaiduMap = mMapView.getMap();

        // 创建poi检索实例，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        findViewById(R.id.tv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city("北京") //必填
                        .keyword("美食") //必填
                        .pageNum(10));
            }
        });


    }

    private PoiBoundSearchOption getSearchParams() {
        PoiBoundSearchOption poiBoundSearchOption = new PoiBoundSearchOption();
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(40.0484459, 116.302072)).include(new LatLng(40.0506675, 116.304317)).build();
        poiBoundSearchOption.bound(bounds);
        poiBoundSearchOption.keyword("加油站");
        return poiBoundSearchOption;
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();

            //创建PoiOverlay对象
            PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);

            //设置Poi检索数据
            poiOverlay.setData(poiResult);

            //将poiOverlay添加至地图并缩放至合适级别
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
