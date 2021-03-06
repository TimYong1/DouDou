package com.example.doudouvideo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.doudouvideo.modle.DDModel;
import com.example.doudouvideo.requst.DDPost;
import com.example.doudouvideo.utils.DDPackageUtils;
import com.example.doudouvideo.update.DDUpdateApk;
import com.example.doudouvideo.utils.DDTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import cn.rongcloud.rtc.stream.local.RongRTCCapture;
import io.rong.imlib.RongIMClient;

public class DDMainActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private FrameLayout frameyout_bigVideo, frameyout_smallVideoOne, frameyout_smallVideoTow, frameyout_smallVideoThree, frameyout_smallVideoFour;
    private View onFousView;
    private Button btn_speaker, btn_pixels, btn_microphone, btn_mute, btn_camera;

    private RCRTCRoom rcrtcRoom = null;
    private RCRTCConfig.Builder configBuilder;
    private RCRTCVideoStreamConfig.Builder videoConfigBuilder;
    private RCRTCVideoView rongRTCVideoView;
    private DDModel aDDModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getUUID();

        DDApplication application = (DDApplication) this.getApplication();
        aDDModel = application.getaDDModel();
        aDDModel.roomId = "123";
        aDDModel.name = userId;
        aDDModel.userId = userId;
        aDDModel.portraitUri = userId;

        String appVersion = DDPackageUtils.getVersionCode(this);
        TextView version_textview = findViewById(R.id.version_textview);
        version_textview.setText("V " + appVersion);

        checkPermissions();
        frameyout_bigVideo = (FrameLayout) findViewById(R.id.frameyout_bigVideo);
        frameyout_smallVideoOne = (FrameLayout) findViewById(R.id.frameyout_smallVideoOne);
        frameyout_smallVideoTow = (FrameLayout) findViewById(R.id.frameyout_smallVideoTow);
        frameyout_smallVideoThree = (FrameLayout) findViewById(R.id.frameyout_smallVideoThree);
        frameyout_smallVideoFour = (FrameLayout) findViewById(R.id.frameyout_smallVideoFour);

        //???????????????
        onFousView = findViewById(R.id.id_focus);

        //???????????????
        btn_speaker = (Button) findViewById(R.id.btn_speaker);
        btn_speaker.setOnFocusChangeListener(this);
        //???????????????
        btn_pixels = (Button) findViewById(R.id.btn_pixels);
        btn_pixels.setOnFocusChangeListener(this);
        //???????????????
        btn_microphone = (Button) findViewById(R.id.btn_microphone);
        btn_microphone.setOnFocusChangeListener(this);
        //????????????
        btn_mute = (Button) findViewById(R.id.btn_mute);
        btn_mute.setOnFocusChangeListener(this);
        //???????????????
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_camera.setOnFocusChangeListener(this);

//        if (!isPhoneOne()) {
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)btn_microphone.getLayoutParams();
//            layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            btn_microphone.setLayoutParams(layoutParams);
//
//            btn_camera.setVisibility(View.GONE);
//        }

        DDUpdateApk.uptateApk(this, true, new DDUpdateApk.DDCallback(){
            @Override
            public void callBack() {
                getToken();
            };
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            DDTools.focusAnimator(v,onFousView);
        }
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_speaker: {
                if (view.isSelected()) {
                    view.setSelected(false);
                } else {
                    view.setSelected(true);
                }
                //???????????????????????????
                RongRTCCapture.getInstance().setEnableSpeakerphone(!view.isSelected());
            }
            break;
            case R.id.btn_pixels: {
                Button btn = (Button)view;
                if (btn.getText().toString().equals("??????")) {
                    btn.setText("??????");
                    screenPixels(1, 0, 0);
                }else if (btn.getText().toString().equals("??????")) {
                    btn.setText("??????");
                    screenPixels(2, 0, 0);
                }else if (btn.getText().toString().equals("??????")) {
                    btn.setText("??????");
                    screenPixels(3, 0, 0);
                }else if (btn.getText().toString().equals("??????")) {
                    btn.setText("??????");
                    screenPixels(4, 0, 0);
                }
            }
            break;
            case R.id.btn_microphone: {
                if (view.isSelected()) {
                    view.setSelected(false);
                } else {
                    view.setSelected(true);
                }
                //true ??????????????? false ???????????????
                RongRTCCapture.getInstance().muteMicrophone(view.isSelected());
            }
            break;
            case R.id.btn_mute: {
                if (view.isSelected()) {
                    view.setSelected(false);
                } else {
                    view.setSelected(true);
                }
                //?????????????????????????????????
                RongRTCCapture.getInstance().muteAllRemoteAudio(view.isSelected());
            }
            break;
            case R.id.btn_camera: {
                //?????????????????????
                RongRTCCapture.getInstance().switchCamera();
            }
            break;
            default:
                break;
        }
    }

    //????????????id????????????token
    private void getToken() {
        DDApplication application = (DDApplication) this.getApplication();
        DDModel aDDModel = application.getaDDModel();
        aDDModel.tokenUrl = "https://api-bj.ronghub.com/user/getToken.json";

        DDPost.GetRongCloudToken(this, aDDModel.name, aDDModel.userId, aDDModel.portraitUri, new DDPost.DDCallback() {
            @Override
            public void callBackSuccess(JSONObject jsonobj) {
                try {
                    String code = jsonobj.getString("code");
                    String token = jsonobj.getString("token");
                    if (code.equals("200")) {
                        rongConnect(token);
                    } else {
                        Toast.makeText(DDMainActivity.this, "??????token??????! code:" + code, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DDMainActivity.this, "??????token???????????????????????????!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void callBackFailure(String message) {
                Toast.makeText(DDMainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //????????????token???????????????
    private void rongConnect(String cacheToken) {
        if (getApplicationInfo().packageName.equals(DDApplication.getCurProcessName(getApplicationContext()))) {
            RongIMClient.connect(cacheToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(DDMainActivity.this, "????????????!", Toast.LENGTH_SHORT).show();

                    if (RongIMClient.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                        joinRoom();
                    }
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                    Toast.makeText(DDMainActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
                    Toast.makeText(DDMainActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //????????????
    private void joinRoom() {
        configBuilder = RCRTCConfig.Builder.create();
        //???????????????
        configBuilder.enableHardwareDecoder(true);
        //???????????????
        configBuilder.enableHardwareEncoder(true);
        RCRTCEngine.getInstance().init(getApplicationContext(), configBuilder.build());

        videoConfigBuilder = RCRTCVideoStreamConfig.Builder.create();
        //????????????
        videoConfigBuilder.setVideoFps(RCRTCVideoFps.Fps_15);
        //???????????????; ??????????????????; ??????????????????. ??????:https://support.rongcloud.cn/ks/MTA3OA==
        screenPixels(2,0, 0);
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoConfig(videoConfigBuilder.build());

        // ??????????????????????????????
        rongRTCVideoView = new RCRTCVideoView(getApplicationContext());
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoView(rongRTCVideoView);

        frameyout_smallVideoOne.setVisibility(View.VISIBLE);
        frameyout_smallVideoOne.removeAllViews();
        frameyout_smallVideoOne.addView(rongRTCVideoView);

        RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);
        //mRoomId,?????? 64 ????????????????????????`A-Z`???`a-z`???`0-9`???`+`???`=`???`-`???`_`
        RCRTCEngine.getInstance().joinRoom(aDDModel.roomId, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(RCRTCRoom rcrtcRoom) {
                Toast.makeText(DDMainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                DDMainActivity.this.rcrtcRoom = rcrtcRoom;
                rcrtcRoom.registerRoomListener(roomEventsListener);
                //???????????????
                RongRTCCapture.getInstance().startCameraCapture();
                //???????????????
                //RongRTCCapture.getInstance().stopCameraCapture();
                //????????????????????????????????????????????????
                publishDefaultAVStream(rcrtcRoom);
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????.
                subscribeAVStream(rcrtcRoom);
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(DDMainActivity.this, "?????????????????????" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //????????????
    private void leaveRoom() {
        RCRTCEngine.getInstance().leaveRoom(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DDMainActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
                        rcrtcRoom = null;
                    }
                });
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(DDMainActivity.this, "?????????????????????" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publishDefaultAVStream(RCRTCRoom rcrtcRoom) {
        rcrtcRoom.getLocalUser().publishDefaultStreams(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(DDMainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(DDMainActivity.this, "???????????????" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
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
            // ???????????????????????????????????????????????????
        } else {
            // ????????????????????????????????????????????????
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
         * ???????????????????????????
         *
         * @param rcrtcRemoteUser ????????????
         * @param list    ???????????????
         */
        @Override
        public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
            //TODO ??????????????????????????????FrameLayout??????
            if (list.size() > 0) {
                showRemoteUser(list);
            }

            //TODO ?????????????????????????????????????????????
            rcrtcRoom.getLocalUser().subscribeStreams(list, new IRCRTCResultCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DDMainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(RTCErrorCode rtcErrorCode) {
                    Toast.makeText(DDMainActivity.this, "???????????????" + rtcErrorCode, Toast.LENGTH_SHORT).show();
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

        }

        /**
         * ??????????????????
         *
         * @param rcrtcRemoteUser ????????????
         */
        @Override
        public void onUserJoined(RCRTCRemoteUser rcrtcRemoteUser) {
            Toast.makeText(DDMainActivity.this, "?????????" + rcrtcRemoteUser.getUserId() + " ????????????", Toast.LENGTH_SHORT).show();
        }

        /**
         * ??????????????????
         *
         * @param rcrtcRemoteUser ????????????
         */
        @Override
        public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {

        }

        @Override
        public void onUserOffline(RCRTCRemoteUser rcrtcRemoteUser) {

        }

        /**
         * ????????????????????? ?????????????????????
         * @param i ?????????
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
            if (remoteUser.getStreams().size() > 0) {
                inputStreams.addAll(remoteUser.getStreams());
            }
        }

        if (inputStreams.size() > 0) {
            //TODO ??????????????????????????????FrameLayout??????
            showRemoteUser(inputStreams);

            rcrtcRoom.getLocalUser().subscribeStreams(inputStreams, new IRCRTCResultCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DDMainActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(RTCErrorCode errorCode) {
                    Toast.makeText(DDMainActivity.this, "???????????????" + errorCode.getReason(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //TODO ??????????????????????????????FrameLayout??????
    private void showRemoteUser(List<RCRTCInputStream> inputStreams) {
        for (RCRTCInputStream inputStream : inputStreams) {
            if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                //?????????????????????????????????????????????
                ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);

                //??????VideoView????????????stream
                RCRTCVideoView remoteVideoView = new RCRTCVideoView(getApplicationContext());
                ((RCRTCVideoInputStream) inputStream).setVideoView(remoteVideoView);

                //??????????????????????????????FrameLayout??????
                frameyout_bigVideo.setVisibility(View.VISIBLE);
                frameyout_bigVideo.removeAllViews();
                frameyout_bigVideo.addView(remoteVideoView);
            }
        }

        if (isPhoneOne()) {
            //???????????????????????????FrameLayout??????
            frameyout_smallVideoOne.setVisibility(View.VISIBLE);
            frameyout_smallVideoOne.removeAllViews();
            frameyout_smallVideoOne.addView(rongRTCVideoView);
        }
    }

    /**
     * ??????????????????ID
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private String getUUID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 ???
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 ??????serial???
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial?????????????????????
            serial = "serial"; // ?????????????????????
        }
        //?????????????????????????????????15?????????
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * ???????????????????????????????????????
     * @return
     */
    public boolean isPhone() {
        TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * ???????????????????????????????????????
     * @return
     */
    private boolean isPhoneOne() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);

        // ??????6????????????Pad?????????
        if (screenInches >= 6.0) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????; ??????????????????; ??????????????????. ??????:https://support.rongcloud.cn/ks/MTA3OA==
     */
    private void screenPixels(int level, int SW, int SH) {
        if (videoConfigBuilder == null) {
            return;
        }

        if (level > 0) {
            if (level == 1) {
                SW = 120;
                SH = 400;
            }else if (level == 2) {
                SW = 200;
                SH = 900;
            }else if (level == 3) {
                SW = 250;
                SH = 2200;
            }else if (level == 4) {
                SW = 132;
                SH = 176;
            }
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (SW < 1 || SH < 1) {
            SW = dm.widthPixels;
            SH = dm.heightPixels;
        }

        if (SW*SH <= 132*176) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_132_176);
            videoConfigBuilder.setMinRate(80);
            videoConfigBuilder.setMaxRate(150);
        }else if (SW*SH <= 144*176) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_144_176);
            videoConfigBuilder.setMinRate(80);
            videoConfigBuilder.setMaxRate(150);
        }else if (SW*SH <= 144*256) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_144_256);
            videoConfigBuilder.setMinRate(120);
            videoConfigBuilder.setMaxRate(240);
        }else if (SW*SH <= 180*320) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_180_320);
            videoConfigBuilder.setMinRate(120);
            videoConfigBuilder.setMaxRate(280);
        }else if (SW*SH <= 240*240) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_240_240);
            videoConfigBuilder.setMinRate(120);
            videoConfigBuilder.setMaxRate(280);
        }else if (SW*SH <= 240*320) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_240_320);
            videoConfigBuilder.setMinRate(120);
            videoConfigBuilder.setMaxRate(400);
        }else if (SW*SH <= 360*480) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_360_480);
            videoConfigBuilder.setMinRate(150);
            videoConfigBuilder.setMaxRate(650);
        }else if (SW*SH <= 368*480) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_368_480);
            videoConfigBuilder.setMinRate(150);
            videoConfigBuilder.setMaxRate(650);
        }else if (SW*SH <= 360*640) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_360_640);
            videoConfigBuilder.setMinRate(180);
            videoConfigBuilder.setMaxRate(800);
        }else if (SW*SH <= 368*640) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_368_640);
            videoConfigBuilder.setMinRate(180);
            videoConfigBuilder.setMaxRate(800);
        }else if (SW*SH <= 480*480) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_480);
            videoConfigBuilder.setMinRate(180);
            videoConfigBuilder.setMaxRate(800);
        }else if (SW*SH <= 480*640) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_640);
            videoConfigBuilder.setMinRate(200);
            videoConfigBuilder.setMaxRate(900);
        }else if (SW*SH <= 480*720) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_720);
            videoConfigBuilder.setMinRate(200);
            videoConfigBuilder.setMaxRate(1000);
        }else if (SW*SH <= 480*854) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_854);
            videoConfigBuilder.setMinRate(200);
            videoConfigBuilder.setMaxRate(1000);
        }else if (SW*SH <= 720*1280) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_720_1280);
            videoConfigBuilder.setMinRate(250);
            videoConfigBuilder.setMaxRate(2200);
        }else if (SW*SH <= 1280*1920) {
            videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_1280_1920);
            videoConfigBuilder.setMinRate(400);
            videoConfigBuilder.setMaxRate(4000);
        }
    }
}