package com.example.rwt;

import android.app.Application;
import android.content.Context;

/**
 * App
 */
public class App extends Application
{

    public static Context AppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
    }

}
