package com.byod.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.byod.app.listener.MDMReceiver;

/**
 * 从服务器获取并存储策略sharedPreference中
 * @author ifay
 *
 */
@SuppressWarnings("static-access")
public class PolicyUtils {

    private static String TAG = "PolicyUtils";
    public static final String POLICY_RESULT = "policy_result";

    //认证策略
    public static String PREF_AUTH_POLICY_TIME = "auth_policy_time";    //认证策略的创建时间
    public static String PREF_AUTH_CHECK_DEV = "auth_check_dev";
    public static String PREF_AUTH_CHECK_PWD = "auth_check_pwd";
    public static String PREF_AUTH_CHECK_TEL = "auth_check_tel";
    
    //密码策略
    public static String PREF_PWD_POLICY_TIME = "pwd_policy_time";  //密码策略创建时间
    public static String PREF_PWD_SPL_CHAR_NUM = "pwd_spl_char_num";    //特殊字符数目
    public static String PREF_PWD_EXPIRE_TIME = "pwd_expire_time";  //密码超期时间
    public static String PREF_PWD_MAX_LENGTH = "pwd_max_length";    //密码长度
    public static String PREF_PWD_MIN_LENGTH = "pwd_min_length";    //密码长度
    public static String PREF_PWD_STARTBY_CHAR = "pwd_startby_char";    //密码以字母开头
    public static String PREF_PWD_TIRAL_TIME = "pwd_trial_time";    //密码尝试次数
    
    //设备策略
    public static String PREF_DEVICE_POLICY_TIME = "device_policy_time";  //设备策略的创建时间
    public static String PREF_OS_VERSION = "os_version";    //系统版本
    public static String PREF_OS_ISROOTED = "is_rooted";   //是否root
    public static String PREF_DEVICE_TYPE = "device_type";  //设备类型
    public static String PREF_DEVICE_WIFI_ENABLE = "device_wifi_enable";    //是否开启wifi
    public static String PREF_DEVICE_G_ENABLE = "device_g_enable";    //是否开启234G
    public static String PREF_DEVICE_BLUETOOTH_ENABLE = "device_bluetooth_enable";  //是否开启蓝牙
    public static String PREF_DEVICE_ONLINE_MAX = "device_onlion_max";  //允许最大在线设备
    public static String PREF_DEVICE_ACCESS_START_TIME = "device_access_start_time";    //允许开始访问的时间
    public static String PREF_DEVICE_ACCESS_END_TIME = "device_access_end_time";    //允许访问的结束时间
    
    public static int CODE_COMPLIANCED = 0; //满足合规性
    public static Map<Integer,String> sPolicyResult = new HashMap<Integer, String>();
    static{
        sPolicyResult.put(0, "complianced");
    };
    
    private static DevicePolicyManager dpManager;
    private static ComponentName byodAdmin;
    private static SharedPreferences sPrefs;

    static SharedPreferences initSharedPreferences(Context ctx) {
        if (sPrefs == null) {
            sPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        }
        return sPrefs;
    }
    
    //device admin check
    public static boolean isAdminActive(Context context) {
        if (dpManager == null) {
            dpManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        }
        if (byodAdmin == null) {
            byodAdmin = new ComponentName(context, MDMReceiver.class);
        }
        return dpManager.isAdminActive(byodAdmin);
    }


    public static void activateDeviceAdmin(Context context) {
        Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        if (byodAdmin == null) {
            byodAdmin = new ComponentName(context, MDMReceiver.class);
        }
        i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, byodAdmin);
        context.startActivity(i);
    }
    
    /**
     * TODO
     * 本方法需要连接网络，不要直接使用
     * 从服务器请求策略是否有更新，若有，则同步下来，解析json，写到本地的sharedPreference中
     * @return 返回成功或失败
     */
    public static boolean getNewestPolicy() {
        if(localPolicyIsNewest()) {
            Log.d(TAG,"local policy is already newest");
            return true;
        }
        
        return true;
    }

    public static void getNewestPolicyByUser(String userAccount) {
        // TODO 获取用户名下的最新策略
        
    }
    
    /**
     * TODO
     * 检测本地策略是否为最新
     * @return
     */
    public static boolean localPolicyIsNewest() {
        //每次同步策略时,将同步的服务器时间存储到sharedpreference中
        //从服务器请求最新的策略的时间，和本地存储的时间比较，若不同，则返回false
        return true;
    }

    public static int getPolicyInt(Context cxt, String key, int defValue) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getInt(key, defValue);
    }

    public static String getPolicyString(Context cxt, String key,  String defValue) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getString(key, defValue);
    }

    public static boolean getPolicyBool(Context cxt, String key, boolean defValue) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getBoolean(key, defValue);
    }

    public static Long getPolicyLong(Context cxt, String key, Long defValue) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        return prefs.getLong(key, defValue);
    }

    public static Long getLatestPolicyTime(Context cxt, Long defValue) {
        SharedPreferences prefs = initSharedPreferences(cxt);
        Log.d("null pointer", prefs==null?"null":"not null");
        return prefs.getLong(PREF_DEVICE_POLICY_TIME, defValue);
    }


}
