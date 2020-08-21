package com.example.doudouvideo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import io.rong.imkit.RongIM;

public class DDApplication extends Application {

    private static DDApplication instance;
    private UserInfo aUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        initActivityLifecycleCallbacks();

        instance = this;
        aUserInfo = new UserInfo();

        RongIM.init(this, "3argexb63svke");
    }

    /**
     * 在application里监听所以activity生命周期的回调
     */
    private void initActivityLifecycleCallbacks(){
        //添加监听
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //activity创建生命周期
                if(activity instanceof MainActivity){
                    //判断创建的activity对应对象

                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //activity启动生命周期

            }

            @Override
            public void onActivityResumed(Activity activity) {
                //activity恢复生命周期

            }

            @Override
            public void onActivityPaused(Activity activity) {
                //activity暂停生命周期

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //activity停止生命周期

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                //保存activity实例状态

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //activity销毁生命周期

            }
        });
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static DDApplication getInstance() {
        return instance;
    }

    public static void setInstance(DDApplication instance) {
        DDApplication.instance = instance;
    }

    public UserInfo getaUserInfo() {
        return aUserInfo;
    }

    public void setaUserInfo(UserInfo aUserInfo) {
        this.aUserInfo = aUserInfo;
    }
}