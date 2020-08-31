package com.example.doudouvideo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(2000);//使程序休眠五秒
                    //启动MainActivity
                    Intent it = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(it);
                    //关闭当前活动
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        //启动线程
        myThread.start();
    }
}