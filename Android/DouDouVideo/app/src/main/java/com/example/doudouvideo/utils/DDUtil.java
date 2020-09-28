package com.example.doudouvideo.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用工具类
 *
 **/
public class DDUtil {

    private static SimpleDateFormat DateYMDHMSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getYMDHMSSToString(long time) {
        Date d = new Date(time);
        return DateYMDHMSS.format(d);
    }
    public static boolean StringEx(String str) {
        if("".equals(str)||str==null){
            return true;
        }else{
            return false;
        }

    }

    //删除本地照片
    public static boolean deleteLocalPicture(ContentResolver resolver,String path){
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=?",
                new String[] { path }, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = resolver.delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(path);
            result = file.delete();
        }
        return result;
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    public static boolean deleteLocal(File file){
        if(file.exists()){
            if(file.isFile()){
                file.delete();//如果为文件，直接删除
            }else if(file.isDirectory()){
                File []files=file.listFiles();
                for(File file1:files){
                    deleteLocal(file1);//如果为文件夹，递归调用
                }
            }
            file.delete();
            return true;
        }
        return false;
    }
}