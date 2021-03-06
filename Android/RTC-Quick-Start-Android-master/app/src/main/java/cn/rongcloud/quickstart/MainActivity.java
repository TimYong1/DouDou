package cn.rongcloud.quickstart;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.rongcloud.rtc.api.RCRTCConfig.Builder;
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
import com.rtcdemo.R;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ConnectionErrorCode;
import io.rong.imlib.RongIMClient.DatabaseOpenStatus;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

  private static final String[] MANDATORY_PERMISSIONS = {
      "android.permission.MODIFY_AUDIO_SETTINGS",
      "android.permission.RECORD_AUDIO",
      "android.permission.INTERNET",
      "android.permission.CAMERA",
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE,
  };

  private String mRoomId = "112233";
  private RCRTCRoom rcrtcRoom = null;
  private TextView tv_textView;
  private FrameLayout frameyout_localUser, frameyout_remoteUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkPermissions();
    tv_textView = (TextView) findViewById(R.id.tv_textView);
    frameyout_localUser = (FrameLayout) findViewById(R.id.frameyout_localUser);
    frameyout_remoteUser = (FrameLayout) findViewById(R.id.frameyout_remoteUser);
  }

  public void click(View view) {
    switch (view.getId()) {
      case R.id.btn_user1:
        setText("????????????1");
        connect(MyApp.token1);
        break;
      case R.id.btn_user2:
        setText("????????????2");
        connect(MyApp.token2);
        break;
      case R.id.btn_leave:
        leaveRoom();
        break;
      default:
        break;
    }
  }

  private void connect(String token) {
    if (RongIMClient.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
      joinRoom();
      return;
    }
    RongIMClient.connect(token, new ConnectCallback() {
      @Override
      public void onSuccess(String s) {
        setText("????????????");
        joinRoom();
      }

      @Override
      public void onError(ConnectionErrorCode connectionErrorCode) {
        setText("??????IM?????? ???" + connectionErrorCode.name());
      }

      @Override
      public void onDatabaseOpened(DatabaseOpenStatus databaseOpenStatus) {

      }
    });
  }

  private void joinRoom() {

    Builder configBuilder = Builder.create();
    //???????????????
    configBuilder.enableHardwareDecoder(true);
    //???????????????
    configBuilder.enableHardwareEncoder(true);
    RCRTCEngine.getInstance().init(getApplicationContext(), configBuilder.build());

    RCRTCVideoStreamConfig.Builder videoConfigBuilder = RCRTCVideoStreamConfig.Builder.create();
    //???????????????
    videoConfigBuilder.setVideoResolution(RCRTCVideoResolution.RESOLUTION_480_640);
    //????????????
    videoConfigBuilder.setVideoFps(RCRTCVideoFps.Fps_15);
    //?????????????????????480P?????????200
    videoConfigBuilder.setMinRate(200);
    //?????????????????????480P?????????900
    videoConfigBuilder.setMaxRate(900);
    RCRTCEngine.getInstance().getDefaultVideoStream().setVideoConfig(videoConfigBuilder.build());

    // ??????????????????????????????
    RCRTCVideoView rongRTCVideoView = new RCRTCVideoView(getApplicationContext());
    RCRTCEngine.getInstance().getDefaultVideoStream().setVideoView(rongRTCVideoView);

    //TODO ????????????????????????FrameLayout??????????????????????????????????????????
    frameyout_localUser.addView(rongRTCVideoView);
    RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);
    //mRoomId,?????? 64 ????????????????????????`A-Z`???`a-z`???`0-9`???`+`???`=`???`-`???`_`
    RCRTCEngine.getInstance().joinRoom(mRoomId, new IRCRTCResultDataCallback<RCRTCRoom>() {
      @Override
      public void onSuccess(RCRTCRoom rcrtcRoom) {
        setText("??????????????????");
        MainActivity.this.rcrtcRoom = rcrtcRoom;
        rcrtcRoom.registerRoomListener(roomEventsListener);
        //?????????????????????????????????????????????????????????
//        RongRTCCapture.getInstance().startCameraCapture();
        //????????????????????????????????????????????????
        publishDefaultAVStream(rcrtcRoom);
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????.
        subscribeAVStream(rcrtcRoom);
      }

      @Override
      public void onFailed(RTCErrorCode rtcErrorCode) {
        setText("?????????????????????" + rtcErrorCode.getReason());
      }
    });
  }

  private void setText(String str) {
    if (TextUtils.isEmpty(str)) {
      tv_textView.setText("");
      return;
    }
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(tv_textView.getText());
    stringBuffer.append("->");
    stringBuffer.append(str);
    tv_textView.setText(stringBuffer.toString());
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
            Toast.makeText(MainActivity.this, "????????????!", Toast.LENGTH_SHORT).show();
            setText(null);
            rcrtcRoom = null;
          }
        });
      }

      @Override
      public void onFailed(RTCErrorCode rtcErrorCode) {
        setText("?????????????????????" + rtcErrorCode.getReason());
      }
    });
  }

  private void publishDefaultAVStream(RCRTCRoom rcrtcRoom) {
    rcrtcRoom.getLocalUser().publishDefaultStreams(new IRCRTCResultCallback() {
      @Override
      public void onSuccess() {
        setText("??????????????????");
      }

      @Override
      public void onFailed(RTCErrorCode rtcErrorCode) {
        setText("???????????????" + rtcErrorCode.getReason());
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
    if (unGrantedPermissions.size() == 0) { // ???????????????????????????????????????????????????
    } else { // ????????????????????????????????????????????????
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
      for (RCRTCInputStream inputStream : list) {
        if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
          RCRTCVideoView remoteVideoView = new RCRTCVideoView(getApplicationContext());
          frameyout_remoteUser.removeAllViews();
          //??????????????????????????????
          frameyout_remoteUser.addView(remoteVideoView);
          ((RCRTCVideoInputStream) inputStream).setVideoView(remoteVideoView);
          //?????????????????????????????????????????????
          ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
        }
      }
      //TODO ?????????????????????????????????????????????
      rcrtcRoom.getLocalUser().subscribeStreams(list, new IRCRTCResultCallback() {
        @Override
        public void onSuccess() {
          setText("????????????");
        }

        @Override
        public void onFailed(RTCErrorCode rtcErrorCode) {
          setText("???????????????" + rtcErrorCode);
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
     * ??????????????????
     *
     * @param rcrtcRemoteUser ????????????
     */
    @Override
    public void onUserJoined(RCRTCRemoteUser rcrtcRemoteUser) {
      Toast.makeText(MainActivity.this, "?????????" + rcrtcRemoteUser.getUserId() + " ????????????", Toast.LENGTH_SHORT).show();
    }

    /**
     * ??????????????????
     *
     * @param rcrtcRemoteUser ????????????
     */
    @Override
    public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {
      frameyout_remoteUser.removeAllViews();
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
      if (remoteUser.getStreams().size() == 0) {
        continue;
      }
      List<RCRTCInputStream> userStreams = remoteUser.getStreams();
      for (RCRTCInputStream inputStream : userStreams) {
        if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
          //?????????????????????????????????????????????
          ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
          //??????VideoView????????????stream
          RCRTCVideoView videoView = new RCRTCVideoView(getApplicationContext());
          ((RCRTCVideoInputStream) inputStream).setVideoView(videoView);
          //??????????????????????????????
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
        setText("????????????");
      }

      @Override
      public void onFailed(RTCErrorCode errorCode) {
        setText("???????????????" + errorCode.getReason());
      }
    });
  }
}
