package com.example.doudouvideo.requst;

import android.app.Activity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DDGet {
    public static void okhttpGetRequst(Activity activity, String url, final DPCallback aDPCallback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                //.addHeader("User-Agent","xxx")//设置请求头
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            //请求失败时调用
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                aDPCallback.callBackFailure("网络请求失败!");

                Log.e("110", "网络请求失败!: " + e.getMessage());
                Log.e("110", "网络请求失败!: " + e.toString());
            }
            //请求成功时调用
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    aDPCallback.callBackSuccess(json);
                }catch (JSONException e) {
                    aDPCallback.callBackFailure("数据解析失败!");
                }
            }
        });
    }

    public interface DPCallback{
        void callBackSuccess(JSONObject jsonobj);

        void callBackFailure(String message);
    }
}

