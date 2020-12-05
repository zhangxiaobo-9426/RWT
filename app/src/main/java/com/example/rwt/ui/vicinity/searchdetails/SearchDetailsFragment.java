package com.example.rwt.ui.vicinity.searchdetails;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.rwt.R;
import com.example.rwt.ui.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailsFragment extends BaseFragment implements OnGetRoutePlanResultListener {

    private Button button_search_details_navigaton;
    private TextView search_details_title,search_details_time,search_details_location;
    private Double to_Latitude,to_Longitude;

    public SearchDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_search_details_navigaton = view.findViewById(R.id.button_search_details_navigaton);
        search_details_title = view.findViewById(R.id.search_details_title);
        search_details_time = view.findViewById(R.id.search_details_time);
        search_details_location = view.findViewById(R.id.search_details_location);
        button_search_details_navigaton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_searchDetailsFragment_to_navigationFragment);
            }
        });

        Bundle bundle = getArguments();
        search_details_title.setText(bundle.getString("VICINITY_TITLE"));
        search_details_time.setText(bundle.getString("VICINITY_TIME"));
        search_details_location.setText(bundle.getString("VICINITY_LOCATION"));
        to_Latitude = bundle.getDouble("VICINITY_LATITUDE");
        to_Longitude = bundle.getDouble("VICINITY_LONGITUDE");

        RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
        routePlanSearch.drivingSearch(getSearchParams());

    }

    private DrivingRoutePlanOption getSearchParams() {
        DrivingRoutePlanOption params = new DrivingRoutePlanOption();
        //设置起点
        params.from(PlanNode.withLocation(myPos));
        //设置终点
        params.to(PlanNode.withLocation(new LatLng(to_Latitude,to_Longitude)));
        return params;
    }

    //步行检索结果回调方法
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }
    //公交、地铁检索结果回调方法
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }
    //驾车检索结果回调方法
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(drivingRouteResult.getRouteLines().get(0));//获取到路线，第0条路线为最优路线
        overlay.addToMap();  //搜索结果添加到地图
        overlay.zoomToSpan();  //搜索结果在一个屏幕内显示
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
