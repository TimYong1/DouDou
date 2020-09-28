package com.example.doudouvideo.utils;

import android.os.Environment;

import java.io.File;


public class DDConfigurationUtil {
    public static final String SDFILE = Environment.getExternalStoragePublicDirectory("") + File.separator + "hnlx/gt/";
    public static final String APK_PATH = "hnlx/gt/"+ "apk" + File.separator;
    public static final String APK_PATH_ABSOULT = DDConfigurationUtil.SDFILE+ "apk" + File.separator;
}
