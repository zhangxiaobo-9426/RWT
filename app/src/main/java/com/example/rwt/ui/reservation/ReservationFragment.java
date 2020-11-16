package com.example.rwt.ui.reservation;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
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

public class ReservationFragment extends Fragment implements OnGetPoiSearchResultListener {

    private ReservationViewModel mViewModel;

    // 声明 PoiSearch
    private MapView mapView = null;
    private BaiduMap baiduMap = null;

    // 声明 PoiSearch
    private PoiSearch mPoiSearch = null;


    public static ReservationFragment newInstance() {
        return new ReservationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reservation_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ReservationViewModel.class);
        // TODO: Use the ViewModel

        mapView = getView().findViewById(R.id.r_bMapView);

        baiduMap = mapView.getMap();
        //显示的地图样式
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置是否显示交通图
        baiduMap.setTrafficEnabled(true);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
//        mPoiSearch.searchInCity(new PoiCitySearchOption()
//                .city("北京") //必填
//                .keyword("美食") //必填
//                .pageNum(10));


//                mPoiSearch.searchNearby(new PoiNearbySearchOption()
//                 .location(new LatLng(39.915446, 116.403869))
//                .radius(1000)
//                .keyword("停车场")
//                .pageNum(10));
//

        /**
         * 设置矩形检索区域
         */
        LatLngBounds searchBounds = new LatLngBounds.Builder()
                .include(new LatLng( 39.92235, 116.380338 ))
                .include(new LatLng( 39.947246, 116.414977))
                .build();

/**
 * 在searchBounds区域内检索餐厅
 */
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchBounds)
                .keyword("停车场"));

    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            baiduMap.clear();

            //创建PoiOverlay对象
            PoiOverlay poiOverlay = new PoiOverlay(baiduMap);

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
