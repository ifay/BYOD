/**
 *
 */

package com.byod.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ifay
 */
public class CommonUtils {

    public static String PKG_NAME = "com.byod";

//    public static String ONLINE_SERVER = "http://172.16.42.9:8080"; // TODO
    public static String ONLINE_SERVER = "http://10.0.0.16:8080"; // TODO
    public static String IAM_URL = ONLINE_SERVER+"/IAM/ws/webservice";
    public static String IAM_NAMESPACE = "http://inter.webservice.iam.qrry.com/";
    public static String UIA_URL = ONLINE_SERVER+"/UIA"; //TODO
    public static String INFO_URL = ONLINE_SERVER+"/INFO"; //TODO

    // （设备）策略
    public static String POLICY_PREF_NAME = "policy";

    /*
     * 服务器指令 1. reset-pwd 2. force-lock 3. wipe-data 4.remove byod
     * 5.disable-camera 6.refresh GPS
     */
    public static int COMMAND_RESET_PWD = 1;
    public static int COMMAND_FORCE_LOCK = 2;
    public static int COMMAND_WIPE_DATA = 3;
    public static int COMMAND_REMOVE_BYOD = 4;
    public static int COMMAND_DISABLE_CAMERA = 5;
    public static int COMMAND_REFRESH_GPS = 6;

    // 通用code
    public static boolean SUCCESS = true;
    public static boolean FAIL = false;

    // 服务
    public static String ACTION_START_SERVICE = "com.byod.action.START";
    public static String ACTION_POLL_SERVICE = "com.byod.action.POLLING";

    public static final int POLL_PEER = 300; // peer-seek interval, 5min


    private static String TAG = "CommonUtils";

    public static final String PREF_KEY_USERACCOUNT = "useraccount"; // 账户名
    public static final String PREF_KEY_PASSWORD = "password";

    private static SharedPreferences sPrefs;

    public static SharedPreferences initSharedPreferences(Context ctx) {
        if (sPrefs == null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        }
        return sPrefs;
    }

    public static void setPrefString(Context context, String key, String value) {
        SharedPreferences prefs = initSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setPrefLong(Context context, String key, Long value) {
        SharedPreferences prefs = initSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static void setPrefBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = initSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static void setPrefInt(Context context, String key, int value) {
        SharedPreferences prefs = initSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static String getPrefString(Context context, String key, String defValue) {
        SharedPreferences prefs = initSharedPreferences(context);
        return prefs.getString(key, defValue);
    }
    
    public static Boolean getPrefBoolean(Context context, String key, Boolean defValue) {
        SharedPreferences prefs = initSharedPreferences(context);
        return prefs.getBoolean(key, defValue);
    }
    
    public static Long getPrefLong(Context context, String key, Long defValue) {
        SharedPreferences prefs = initSharedPreferences(context);
        return prefs.getLong(key, defValue);
    }
    
    public static int getPrefInt(Context context, String key, int defValue) {
        SharedPreferences prefs = initSharedPreferences(context);
        return prefs.getInt(key, defValue);
    }

    /**
     * delete all local policy data
     */
    public static void deleteLocalPolicy(Context ctx) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(ctx);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    // 卸载自身 TODO 不能实现
    public static void uninstallBYOD(Context context) {
        Uri packageUri = Uri.parse("package:" + CommonUtils.PKG_NAME);
        Log.d(TAG, "uninstall" + packageUri.toString());
        Intent i = new Intent(Intent.ACTION_DELETE, packageUri);
        context.startActivity(i);
    }

    // MD5加密
    public static String cryptMD5(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md5.update(src.getBytes());
        byte[] md5Data = md5.digest(); // 密文
        // 密文转换为十六进制字符串
        return getHexString(md5Data);
    }

    // SHA1加密
    public static String cryptSH1(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md5.update(src.getBytes());
        byte[] md5Data = md5.digest(); // 密文
        // 密文转换为十六进制字符串
        return getHexString(md5Data);
    }

    private static String getHexString(byte[] b) {
        StringBuffer strb = new StringBuffer();
        int temp;
        for (int i = 0; i < b.length; i++) {
            temp = 0xFF & b[i];
            if (temp <= 0xF) {
                strb.append('0');
            }
            strb.append(b[i]);
        }
        return strb.toString();
    }



}
