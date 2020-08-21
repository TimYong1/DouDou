package cn.rongcloud.quickstart;

import android.app.Application;
import io.rong.imlib.RongIMClient;

public class MyApp extends Application {

  public static final String appkey = "3argexb63svke";
  public static final String token1 = "3Qn0zy3knBlKFy14KDyz6H91EIqaU0u6@d8fm.cn.rongnav.com;d8fm.cn.rongcfg.com";
  public static final String token2 = "0FfSQj/UpE1KFy14KDyz6GiGvn1xj4rI@d8fm.cn.rongnav.com;d8fm.cn.rongcfg.com";

  @Override
  public void onCreate() {
    super.onCreate();
    RongIMClient.init(this, appkey, false);
  }
}
