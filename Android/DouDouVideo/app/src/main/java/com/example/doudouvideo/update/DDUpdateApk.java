package com.example.doudouvideo.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.doudouvideo.requst.DDGet;
import com.example.doudouvideo.utils.DDPackageUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DDUpdateApk {
    public static void uptateApk(final Activity activity, final boolean updateForced, final DDUpdateApk.DDCallback aDPCallback) {
        DDGet.okhttpGetRequst(activity, "http://api.bq04.com/apps/latest/5f4ca825b2eb464a3b49b099?api_token=e5f1b81ae45a45d787cde3a40b8cfbe4", new DDGet.DPCallback() {
            @Override
            public void callBackSuccess(JSONObject jsonobj) {
                try {
                    String fiimVersion = jsonobj.getString("versionShort");
                    fiimVersion = fiimVersion.replace(".", "");
                    if (fiimVersion.length() == 1) {
                        fiimVersion = fiimVersion + "00";
                    } else if (fiimVersion.length() == 2) {
                        fiimVersion = fiimVersion + "0";
                    }
                    String appVersion = DDPackageUtils.getVersionCode(activity);
                    appVersion = appVersion.replace(".", "");
                    if (fiimVersion != null && appVersion != null && !fiimVersion.isEmpty() && !appVersion.isEmpty() && Float.valueOf(fiimVersion) > Float.valueOf(appVersion)) {
                        String update_url = jsonobj.getString("install_url");
                        uptateAlertView(activity, updateForced, update_url, aDPCallback);
                    } else {
                        aDPCallback.callBack();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "获取token失败，数据解析错误!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void callBackFailure(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void uptateAlertView(final Activity activity, final boolean updateForced, final String url, final DDUpdateApk.DDCallback aDPCallback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 构造对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("软件更新");
                builder.setMessage("有新版本,建议更新!");
                // 更新
                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 显示下载对话框
                        DDDownloadApk.downloadForAutoInstall(activity, url);
                    }
                });
                // 稍后更新
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!updateForced) {
                            aDPCallback.callBack();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public interface DDCallback {
        void callBack();
    }
}
