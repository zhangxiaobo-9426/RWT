package com.example.rwt.ui.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.rwt.MainActivity;
import com.example.rwt.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Timer timer=new Timer();   //创建Timer对象，用于设置启动界面显示的时间
        //创建TimerTask对象，用于实现启动界面与游戏主界面的跳转
        TimerTask timerTask =new TimerTask() {
            @Override
            public void run() {
                //从启动界面跳转到游戏主界面
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();                   //关闭启动界面
            }
        };
        timer.schedule(timerTask,2000);   //设置显示启动界面2秒后，跳转主界面

    }
}
