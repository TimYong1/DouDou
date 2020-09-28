package com.example.doudouvideo.requst;

import android.app.Activity;

import com.example.doudouvideo.DDApplication;
import com.example.doudouvideo.modle.DDModel;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DDPost {
    public static void GetRongCloudToken(Activity activity, String userId, String userName, String portraitUri, final DDCallback aDDCallback) {
        DDApplication application = (DDApplication) activity.getApplication();
        DDModel aDDModel = application.getaDDModel();

        String url = aDDModel.tokenUrl;
        String App_Key = aDDModel.appKey;
        String App_Secret = aDDModel.appSecret;
        String Timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳，从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。
        String Nonce = String.valueOf(Math.floor(Math.random() * 1000000));//随机数，无长度限制。
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        Logger.i(Signature);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("name", userId)
                .add("userId", userName)
                .add("portraitUri", portraitUri)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("App-Key", App_Key)
                .addHeader("Timestamp", Timestamp)
                .addHeader("Nonce", Nonce)
                .addHeader("Signature", Signature)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                aDDCallback.callBackFailure("网络请求失败!");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    aDDCallback.callBackSuccess(json);
                }catch (JSONException e) {
                    aDDCallback.callBackFailure("数据解析失败!");
                }
            }
        });
    }

    public interface DDCallback {
        void callBackSuccess(JSONObject jsonobj);

        void callBackFailure(String message);
    }

    //SHA1加密//http://www.rongcloud.cn/docs/server.html#通用_API_接口签名规则
    private static String sha1(String data){
        StringBuffer buf = new StringBuffer();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(data.getBytes());
            byte[] bits = md.digest();
            for(int i = 0 ; i < bits.length;i++){
                int a = bits[i];
                if(a<0) a+=256;
                if(a<16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
        }catch(Exception e){

        }
        return buf.toString();
    }
}

