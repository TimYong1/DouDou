package com.example.doudouvideo.update;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.example.doudouvideo.utils.DDConfigurationUtil;
import com.example.doudouvideo.utils.DDUtil;

import java.io.File;

public class DDDownloadApk {

    /**
     * 下载更新apk包
     * 权限:1,<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
     *
     * @param context
     * @param apkUrl
     */
    public static void downloadForAutoInstall(Context context, String apkUrl) {
        DDUtil.deleteLocal(new File(DDConfigurationUtil.APK_PATH_ABSOULT+"豆豆.apk"));//删除旧的apk
        Uri uri = Uri.parse(apkUrl);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(request.NETWORK_MOBILE | request.NETWORK_WIFI);
        //设置是否允许漫游
        request.setAllowedOverRoaming(true);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(apkUrl));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("豆豆");
        request.setVisibleInDownloadsUi(true);
        //sdcard目录下的download文件夹
        request.setDestinationInExternalPublicDir(DDConfigurationUtil.APK_PATH, "豆豆.apk");
        // 将下载请求放入队列
        downloadManager.enqueue(request);
    }

}


