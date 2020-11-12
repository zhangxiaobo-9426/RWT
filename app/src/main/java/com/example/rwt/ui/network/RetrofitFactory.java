package com.example.rwt.ui.network;

import androidx.annotation.NonNull;

import com.example.rwt.App;
import com.example.rwt.AppConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit 封装工厂类
 */
public class RetrofitFactory
{

    private static final Interceptor cacheControlInterceptor = new Interceptor()
    {
        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();
            // 判断网络是否连接
            if (!NetWorkUtil.isNetworkConnected(App.AppContext))
            {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }

            Response originalResponse = chain.proceed(request);
            if (NetWorkUtil.isNetworkConnected(App.AppContext))
            {
                // 有网络时 设置缓存为默认值
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            }
            else
            {
                // 无网络时 设置超时为1周
                int maxStale = 60 * 60 * 24 * 7;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    private volatile static Retrofit retrofit;

    @NonNull
    public static Retrofit getRetrofit()
    {
        if (retrofit == null)
        {
            synchronized (RetrofitFactory.class)
            {
                if (retrofit == null)
                {
                    // 指定缓存路径,缓存大小 50Mb
//                    Cache cache = new Cache(new File(App.AppContext.getCacheDir(), "HttpCache"),
//                                            1024 * 1024 * 50);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                            .cookieJar(cookieJar)
//                            .cache(cache).addInterceptor(cacheControlInterceptor)
                            // 设置连接超市时间
                            .connectTimeout(10, TimeUnit.SECONDS)
                            // 设置读超时时间
                            .readTimeout(15, TimeUnit.SECONDS)
                            // 设置写超时时间
                            .writeTimeout(15, TimeUnit.SECONDS).retryOnConnectionFailure(true);

                    // Log 拦截器
                    if (AppConfig.DEBUG)
                    {

                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        builder.addInterceptor(logging);

                    }

                    retrofit = new Retrofit.Builder().baseUrl(AppConfig.BASE_URL)
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
