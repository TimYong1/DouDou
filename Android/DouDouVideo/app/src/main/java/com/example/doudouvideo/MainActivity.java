package com.example.doudouvideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCRoomEventsListener;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoStreamConfig;
import cn.rongcloud.rtc.api.stream.RCRTCVideoView;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCParamsType.RCRTCVideoFps;
import cn.rongcloud.rtc.base.RCRTCParamsType.RCRTCVideoResolution;
import cn.rongcloud.rtc.base.RCRTCStreamType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.RongIMClient;


public class MainActivity extends AppCompatActivity {
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private RCRTCRoom rcrtcRoom = null;
    private FrameLayout frameyout_localUser, frameyout_remoteUser;

    private DDModel aDDModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = String.valueOf((int)(Math.random()*10));

        DDApplication application = (DDApplication) this.getApplication();
        aDDModel = application.getaDDModel();
        aDDModel.roomId = "111";
        aDDModel.name = userId;
        aDDModel.userId = userId;
        aDDModel.portraitUri = userId;

        checkPermissions();
        frameyout_localUser = (FrameLayout) findViewById(R.id.frameyout_localUser);
        frameyout_remoteUser = (FrameLayout) findViewById(R.id.frameyout_remoteUser);

        getToken();
    }

    public void click(View view) {
//        switch (view.getId()) {
//            case R.id.btn_leave:
//                leaveRoom();
//                break;
//            default:
//                break;
//        }
    }

    //根据用户id获取用户token
    private void getToken() {
        DDApplication application = (DDApplication) this.getApplication();
        DDModel aDDModel = application.getaDDModel();
        aDDModel.tokenUrl = "https://api-bj.ronghub.com/user/getToken.json";

        DDPost.GetRongCloudToken(this, aDDModel.name, aDDModel.userId, aDDModel.portraitUri, new DDPost.DPCallback() {
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

    //根据用户token连接服务器
    private void rongConnect(String cacheToken) {
        if (getApplicationInfo().packageName.equals(DDApplication.getCurProcessName(getApplicationContext()))) {
            RongIMClient.connect(cacheToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(MainActivity.this, "登陆成功!", Toast.LENGTH_SHORT).show();

                    if (RongIMClient.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                        joinRoom();
                    }
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

    //加入房间
    private void joinRoom() {
        RCRTCConfig.Builder configBuilder = RCRTCConfig.Builder.create();
        //是否硬解码
        configBuilder.enableHardwareDecoder(true);
        //是否硬编码
        configBuilder.enableHardwareEncoder(true);
        RCRTCEngine.getInstance().init(getApplicationContext(), configBuilder.build());

        RCRTCVideoStreamConfig.Builder videoConfigBuilder = RCRTCVideoStreamConfig.Builder.create();
        //设置分辨率
        videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_640);
        //设置帧率
        videoConfigBuilder.setVideoFps(RCRTCVideoFps.Fps_15);
        //设置最小码率，480P下推荐200
        videoConfigBuilder.setMinRate(200);
        //设置最大码率，480P下推荐900
        videoConfigBuilder.setMaxRate(900);
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoConfig(videoConfigBuilder.build());

        // 创建本地视频显示视图
        RCRTCVideoView rongRTCVideoView = new RCRTCVideoView(getApplicationContext());
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoView(rongRTCVideoView);

        //TODO 将本地视图添加至FrameLayout布局，需要开发者自行创建布局
        frameyout_localUser.addView(rongRTCVideoView);
        RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);
        //mRoomId,长度 64 个字符，可包含：`A-Z`、`a-z`、`0-9`、`+`、`=`、`-`、`_`
        RCRTCEngine.getInstance().joinRoom(aDDModel.roomId, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(RCRTCRoom rcrtcRoom) {
                Toast.makeText(MainActivity.this, "加入房间成功", Toast.LENGTH_SHORT).show();
                MainActivity.this.rcrtcRoom = rcrtcRoom;
                rcrtcRoom.registerRoomListener(roomEventsListener);
                //加入房间成功后，开启摄像头采集视频数据
                //RongRTCCapture.getInstance().startCameraCapture();
                //加入房间成功后，发布默认音视频流
                publishDefaultAVStream(rcrtcRoom);
                //加入房间成功后，如果房间中已存在用户且发布了音、视频流，就订阅远端用户发布的音视频流.
                subscribeAVStream(rcrtcRoom);
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "加入房间失败：" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveRoom() {
        RCRTCEngine.getInstance().leaveRoom(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        frameyout_localUser.removeAllViews();
                        frameyout_remoteUser.removeAllViews();
                        Toast.makeText(MainActivity.this, "退出成功!", Toast.LENGTH_SHORT).show();
                        rcrtcRoom = null;
                    }
                });
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "退出房间失败：" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publishDefaultAVStream(RCRTCRoom rcrtcRoom) {
        rcrtcRoom.getLocalUser().publishDefaultStreams(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "发布资源成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "发布失败：" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeStreams(RCRTCRoom rcrtcRoom) {
        RCRTCRemoteUser remoteUser = rcrtcRoom.getRemoteUser("003");
        rcrtcRoom.getLocalUser().subscribeStreams(remoteUser.getStreams(), new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {

            }
        });
    }

    List<String> unGrantedPermissions;

    private void checkPermissions() {
        unGrantedPermissions = new ArrayList();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }
        if (unGrantedPermissions.size() == 0) {
            // 已经获得了所有权限，开始加入聊天室
        } else {
            // 部分权限未获得，重新请求获取权限
            String[] array = new String[unGrantedPermissions.size()];
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(array), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        unGrantedPermissions.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                unGrantedPermissions.add(permissions[i]);
            }
        }
        for (String permission : unGrantedPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            }
        }
        if (unGrantedPermissions.size() == 0) {
        }
    }


    private IRCRTCRoomEventsListener roomEventsListener = new IRCRTCRoomEventsListener() {

        /**
         * 房间内用户发布资源
         *
         * @param rcrtcRemoteUser 远端用户
         * @param list    发布的资源
         */
        @Override
        public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
            for (RCRTCInputStream inputStream : list) {
                if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                    RCRTCVideoView remoteVideoView = new RCRTCVideoView(getApplicationContext());
                    frameyout_remoteUser.removeAllViews();
                    //将远端视图添加至布局
                    frameyout_remoteUser.addView(remoteVideoView);
                    ((RCRTCVideoInputStream) inputStream).setVideoView(remoteVideoView);
                    //选择订阅大流或是小流。默认小流
                    ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
                }
            }
            //TODO 按需在此订阅远端用户发布的资源
            rcrtcRoom.getLocalUser().subscribeStreams(list, new IRCRTCResultCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(RTCErrorCode rtcErrorCode) {
                    Toast.makeText(MainActivity.this, "订阅失败：" + rtcErrorCode, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRemoteUserMuteAudio(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {

        }

        @Override
        public void onRemoteUserMuteVideo(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {
        }


        @Override
        public void onRemoteUserUnpublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
            frameyout_remoteUser.removeAllViews();
        }

        /**
         * 用户加入房间
         *
         * @param rcrtcRemoteUser 远端用户
         */
        @Override
        public void onUserJoined(RCRTCRemoteUser rcrtcRemoteUser) {
            Toast.makeText(MainActivity.this, "用户：" + rcrtcRemoteUser.getUserId() + " 加入房间", Toast.LENGTH_SHORT).show();
        }

        /**
         * 用户离开房间
         *
         * @param rcrtcRemoteUser 远端用户
         */
        @Override
        public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {
            frameyout_remoteUser.removeAllViews();
        }

        @Override
        public void onUserOffline(RCRTCRemoteUser rcrtcRemoteUser) {

        }

        /**
         * 自己退出房间。 例如断网退出等
         * @param i 状态码
         */
        @Override
        public void onLeaveRoom(int i) {

        }
    };

    private void subscribeAVStream(RCRTCRoom rtcRoom) {
        if (rtcRoom == null || rtcRoom.getRemoteUsers() == null) {
            return;
        }
        List<RCRTCInputStream> inputStreams = new ArrayList<>();
        for (final RCRTCRemoteUser remoteUser : rcrtcRoom.getRemoteUsers()) {
            if (remoteUser.getStreams().size() == 0) {
                continue;
            }
            List<RCRTCInputStream> userStreams = remoteUser.getStreams();
            for (RCRTCInputStream inputStream : userStreams) {
                if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                    //选择订阅大流或是小流。默认小流
                    ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
                    //创建VideoView并设置到stream
                    RCRTCVideoView videoView = new RCRTCVideoView(getApplicationContext());
                    ((RCRTCVideoInputStream) inputStream).setVideoView(videoView);
                    //将远端视图添加至布局
                    frameyout_remoteUser.addView(videoView);
                }
            }
            inputStreams.addAll(remoteUser.getStreams());
        }

        if (inputStreams.size() == 0) {
            return;
        }
        rcrtcRoom.getLocalUser().subscribeStreams(inputStreams, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                Toast.makeText(MainActivity.this, "订阅失败：" + errorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}