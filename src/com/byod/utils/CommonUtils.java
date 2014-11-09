/**
 * 
 */
package com.byod.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;


/**
 * @author ifay
 *
 */
public class CommonUtils {

    public static String ONLINE_SERVER = "192.168.0.106";    //TODO
    
    //退出系统receiver
    public static String ExitListenerReceiver = "ExitListenerReceiver";
    
    //（设备）策略
    public static String POLICY_PREF_NAME = "policy";
    
    /*
     *  服务器指令
     *  1. reset-pwd
     *  2. force-lock 
     *  3. wipe-data 
     *  4.remove byod 
     *  5.disable-camera
     *  6.refresh GPS
     */
    public static int COMMAND_RESET_PWD = 1;
    public static int COMMAND_FORCE_LOCK = 2;
    public static int COMMAND_WIPE_DATA = 3;
    public static int COMMAND_REMOVE_BYOD = 4;
    public static int COMMAND_DISABLE_CAMERA = 5;
    public static int COMMAND_REFRESH_GPS = 6;
    
    //通用code
    public static boolean SUCCESS = true;
    public static boolean FAIL = false;
    
    public static void exitBYOD(Context context) {
        Intent intent;
        intent = new Intent(context.getPackageName() + "."
                + CommonUtils.ExitListenerReceiver);
        context.sendBroadcast(intent);
    }
    
    //MD5加密
    public static String cryptMD5(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        md5.update(src.getBytes());
        byte[] md5Data = md5.digest();  //密文
        //密文转换为十六进制字符串
        return getHexString(md5Data);
    }

    //SHA1加密
    public static String cryptSH1(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        md5.update(src.getBytes());
        byte[] md5Data = md5.digest();  //密文
        //密文转换为十六进制字符串
        return getHexString(md5Data);
    }
    
    private static String getHexString(byte[] b) {
        StringBuffer strb = new StringBuffer();
        int temp;
        for(int i = 0; i < b.length; i++) {
            temp = 0xFF & b[i];
            if (temp <= 0xF) {
                strb.append('0');
            }
            strb.append(b[i]);
        }
        return strb.toString();
    }


}
