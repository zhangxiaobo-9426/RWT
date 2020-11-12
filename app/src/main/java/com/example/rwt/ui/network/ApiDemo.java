package com.example.rwt.ui.network;

import com.example.rwt.ui.vicinity.entity.VicinityCar;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Api声明示例
 */
public interface ApiDemo
{

//    //https://api.github.com/users/thymleaf/repos
//    @GET("users/{user}/repos")
//    Call<List<Repo>> listRepos(@Path("user") String user);
//
//
//    @GET("users/{user}/repos")
//    Flowable<List<Repo>> getRepos(@Path("user") String user);
//
//
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId);
//
//
//    //https://api.github.com/users/list?sort=desc
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
//
//
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

    @GET("vicinitycar/listVicintycar")
    Flowable<List<VicinityCar>> getCar();
}
