package com.example.rwt.ui.vicinity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.rwt.R;
import com.example.rwt.ui.base.BaseFragment;
import com.example.rwt.ui.network.ApiDemo;
import com.example.rwt.ui.network.RetrofitFactory;
import com.example.rwt.ui.vicinity.entity.VicinityCar;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VicinityFragment extends BaseFragment implements OnGetPoiSearchResultListener {
    private View view;
    //用于获取自己的信息
    private TextView mLocationInfo;

    private VicinityViewModel mViewModel;

    // 声明 PoiSearch
    private PoiSearch mPoiSearch = null;

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
                .include(new LatLng(26.395228, 106.623059))
                .include(new LatLng(26.418656, 106.64584))
                .build();

        /*
         * 在searchBounds区域内检索停车场
         */
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchBounds)
                .keyword("停车场"));


        recyclerView = view.findViewById(R.id.recyclerView);
        // 第一步：指定布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        // 第二步：设置适配器
        adapter = new VicinityAdapter(R.layout.vicinity_item);
        recyclerView.setAdapter(adapter);
        //recyclerView分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

//        loadData();
        loadDatahttp();

    }

    //网络加载
    private void loadDatahttp() {
        // 获取Retrofit对象
        RetrofitFactory.getRetrofit().create(ApiDemo.class)
                .getCar()
                // 切换到IO现场执行网络请求
                .subscribeOn(Schedulers.io())
                // 切换到UI线程执行UI操作
                .observeOn(AndroidSchedulers.mainThread())
                // 获取网络请求结果
                .subscribe(new Consumer<List<VicinityCar>>() {
                               @Override
                               public void accept(List<VicinityCar> repos) throws Exception {

                                   adapter.addData(repos);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
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

    //获取兴趣点信息
    @Override
    public void onGetPoiResult(PoiResult poiResult) {

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            baiduMap.clear();

            //创建PoiOverlay对象
            PoiOverlay poiOverlay = new PoiOverlay(baiduMap) {
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

}
