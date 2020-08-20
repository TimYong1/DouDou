package com.example.doudouvideo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String randomId = String.valueOf((int)(Math.random()*10));

        DDApplication application = (DDApplication) this.getApplication();
        UserInfo aUserInfo = application.getaUserInfo();
        aUserInfo.name = randomId;
        aUserInfo.userId = randomId;
        aUserInfo.portraitUri = randomId;

        Button button = findViewById(R.id.button_call);
        button.setOnClickListener(this);

        TextView myIdTextView = findViewById(R.id.myId);
        myIdTextView.setText("我的呼叫ID:"+aUserInfo.userId);
        this.getToken();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_call: {
                EditText callIdEditText = findViewById(R.id.callId);
                if (callIdEditText.getText().toString().length() < 1) {
                    Toast.makeText(MainActivity.this, "请填写对方的呼叫ID!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RongCallKit.startSingleCall(MainActivity.this, callIdEditText.getText().toString(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO);
            }
            break;
            default:
                break;
        }
    }

    public void getToken() {
        DDApplication application = (DDApplication) this.getApplication();
        UserInfo aUserInfo = application.getaUserInfo();
        GetToken.GetRongCloudToken(aUserInfo.name, aUserInfo.userId, aUserInfo.portraitUri, new GetToken.DPCallback() {
            @Override
            public void callBackSuccess(JSONObject jsonobj) {
                try {
                    String code = jsonobj.getString("code");
                    String token = jsonobj.getString("token");
                    if (code.equals("200")) {
                        rongConnect(token);
                    }else {
                        Toast.makeText(MainActivity.this, "获取token失败! code:"+code, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "获取token失败，数据解析错误!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void callBackFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void rongConnect(String cacheToken) {
        if (getApplicationInfo().packageName.equals(DDApplication.getCurProcessName(getApplicationContext()))) {
            RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(MainActivity.this, "登陆成功!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                    Toast.makeText(MainActivity.this, "登陆失败!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
                    Toast.makeText(MainActivity.this, "登陆失败!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}