package com.byod.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.byod.app.listener.MDMReceiver;

/**
 * 从服务器获取并存储策略sharedPreference中
 *
 * @author ifay
 */
@SuppressWarnings("static-access")
public class PolicyUtils {

    private static String TAG = "PolicyUtils";
    public static final String POLICY_RESULT = "policy_result";
    private static ExecutorService pool = Executors.newCachedThreadPool();

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

    /**
     * DEVICEPOLICYID   VARCHAR2(20)    N           
        DEVICEPOLICYNAME    VARCHAR2(40)    N           
       2 CREATETIME  DATE    N           
       1 MINAPISUPPORTED NUMBER  Y           
       1 DEVICEROOTSUPPORT   NUMBER  Y           0 or 1
       1 DEVICETYPE  NUMBER  Y           1:phone, 0 tablets
       1 ALLOWWIFICONNECT    NUMBER  Y           0 or 1
        1 ALLOWGPRSCONNECT    NUMBER  Y           0 or 1
        1 ALLOWBLUETOOTH  NUMBER  Y           0 or 1
       1 MAXONLINEDEVNUM NUMBER  Y           
        1 ACCESSSTARTTIME NUMBER  Y           24h format
        1 ACCESSENDTIME   NUMBER  Y           
        BAKSTR1 VARCHAR2(40)    Y           
        BAKSTR2 VARCHAR2(40)    Y           
        BAKSTR3 VARCHAR2(40)    Y           
        BAKSTR4 VARCHAR2(40)    Y           
     */
    
    //设备策略
    public static String PREF_DEVICE_POLICY_TIME = "device_policy_time";  //设备策略的创建时间
    public static String PREF_DEVICE_MIN_VERSION = "MINAPISUPPORTED";    //系统最低版本
    public static String PREF_DEVICE_ROOT_SUPPORT = "DEVICEROOTSUPPORT";   //是否root
    public static String PREF_DEVICE_TYPE = "DEVICETYPE";  //设备类型
    public static String PREF_DEVICE_WIFI_ENABLE = "ALLOWWIFICONNECT";    //是否开启wifi
    public static String PREF_DEVICE_GPRS_ENABLE = "ALLOWGPRSCONNECT";    //是否开启GPRS
    public static String PREF_DEVICE_BLUETOOTH_ENABLE = "ALLOWBLUETOOTH";  //是否开启蓝牙
    public static String PREF_DEVICE_ONLINE_MAX = "MAXONLINEDEVNUM";  //允许最大在线设备
    public static String PREF_DEVICE_ACCESS_START_TIME = "ACCESSSTARTTIME";    //允许开始访问的时间
    public static String PREF_DEVICE_ACCESS_END_TIME = "ACCESSENDTIME";    //允许访问的结束时间

    public static int CODE_COMPLIANCED = 0; //满足合规性
    public static Map<Integer, String> sPolicyResult = new HashMap<Integer, String>();

    static {
        sPolicyResult.put(0, "complianced");
    }

    ;

    private static DevicePolicyManager dpManager;
    private static ComponentName byodAdmin;


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
     * 本方法需要连接网络，不要直接使用\
     * 从服务器请求策略是否有更新，若有，则同步下来，解析json，写到本地的sharedPreference中
     *
     * @throws Exception 
     */
    public static void getDevicePolicy(Context context,String deviceID) throws Exception {
        try {
            if (localPolicyIsNewest(context, deviceID)) {
                Log.d(TAG, "local policy is already newest");
                return;
            } else {
                PropertyInfo[] property = new PropertyInfo[1];
                property[0] = new PropertyInfo();
                property[0].setName("deviceID");
                property[0].setValue(deviceID);
                property[0].setType(PropertyInfo.STRING_CLASS);
                WebConnectCallable task = new WebConnectCallable(CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "getDevicePolicy", property);
                if (pool == null) {
                    pool = Executors.newCachedThreadPool();
                }
                Future<String> future = pool.submit(task);
                String policyJson = future.get();
                //parase and store policyJson
                paraseAndSavePolicy(context,policyJson);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据Pref中的值进行安全策略检测
     * @param context
     * @return
     */
    public static String checkPolicy(Context context) {
        Log.d(TAG,"checkPolicy");
        //PREF_DEVICE_ACCESS_START_TIME
        int startTime = CommonUtils.getPrefInt(context, PREF_DEVICE_ACCESS_START_TIME, -1);
        int endTime = CommonUtils.getPrefInt(context, PREF_DEVICE_ACCESS_END_TIME, -1);
        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (curHour < startTime ) {
            return PREF_DEVICE_ACCESS_START_TIME;
        } else if (curHour > endTime) {
            return PREF_DEVICE_ACCESS_END_TIME;
        }
        //PREF_DEVICE_MIN_VERSION
        int version = CommonUtils.getPrefInt(context, PREF_DEVICE_MIN_VERSION, -1);
        if (DeviceUtils.getAPIVersion() < version) {
            return PREF_DEVICE_MIN_VERSION;
        }
        //PREF_DEVICE_ROOT_SUPPORT
        boolean isRoot = CommonUtils.getPrefBoolean(context, PREF_DEVICE_ROOT_SUPPORT, false);//默认不允许root
        if (DeviceUtils.isRooted() != isRoot) {
            return PREF_DEVICE_ROOT_SUPPORT;
        }
        //PREF_DEVICE_WIFI_ENABLE
        boolean isWifiEnabled = CommonUtils.getPrefBoolean(context, PREF_DEVICE_BLUETOOTH_ENABLE, true);//注意：默认不应该允许开Wifi，但是为了测试，需要打开
        if (DeviceUtils.isWifiEnabled(context) != isWifiEnabled) {
            return PREF_DEVICE_WIFI_ENABLE;
        }
        
        //PREF_DEVICE_GPRS_ENABLE
        boolean isGPRSEnabled = CommonUtils.getPrefBoolean(context, PREF_DEVICE_GPRS_ENABLE, true);//默认为true
        if (DeviceUtils.isGPRSEnabled(context) != isGPRSEnabled) {
            return PREF_DEVICE_GPRS_ENABLE;
        }
        
        //PREF_DEVICE_BLUETOOTH_ENABLE
        boolean isBlueToothEnabled = CommonUtils.getPrefBoolean(context, PREF_DEVICE_BLUETOOTH_ENABLE, false);//默认为false
        if (DeviceUtils.isBlueToothEnabled(context) != isBlueToothEnabled) {
            return PREF_DEVICE_BLUETOOTH_ENABLE;
        }
        return null;
    }


    private static void paraseAndSavePolicy(Context context, String policyString) {
        try {
            JSONObject policyJson = new JSONObject(policyString);
            Log.d(TAG,"string to JSON:"+policyJson.toString());
            //update policy time first
            Long createTime = Long.parseLong(policyJson.getString("CREATETIME"));
            CommonUtils.setPrefLong(context, PREF_DEVICE_POLICY_TIME, createTime);
            //store in Preferences
            CommonUtils.setPrefBoolean(context, PREF_DEVICE_ROOT_SUPPORT, policyJson.getString(PREF_DEVICE_ROOT_SUPPORT).trim().equals("1"));
            CommonUtils.setPrefBoolean(context, PREF_DEVICE_TYPE, policyJson.getString(PREF_DEVICE_TYPE).trim().equals("1"));//true=phone, false=tablets
            CommonUtils.setPrefBoolean(context, PREF_DEVICE_WIFI_ENABLE, policyJson.getString(PREF_DEVICE_WIFI_ENABLE).trim().equals("1"));
            CommonUtils.setPrefBoolean(context, PREF_DEVICE_GPRS_ENABLE, policyJson.getString(PREF_DEVICE_GPRS_ENABLE).trim().equals("1"));
            CommonUtils.setPrefBoolean(context, PREF_DEVICE_BLUETOOTH_ENABLE, policyJson.getString(PREF_DEVICE_BLUETOOTH_ENABLE).trim().equals("1"));
            CommonUtils.setPrefInt(context, PREF_DEVICE_ONLINE_MAX, Integer.parseInt(policyJson.getString(PREF_DEVICE_ONLINE_MAX).trim()));
            CommonUtils.setPrefInt(context, PREF_DEVICE_ACCESS_START_TIME, Integer.parseInt(policyJson.getString(PREF_DEVICE_ACCESS_START_TIME).trim()));
            CommonUtils.setPrefInt(context, PREF_DEVICE_ACCESS_END_TIME, Integer.parseInt(policyJson.getString(PREF_DEVICE_ACCESS_END_TIME).trim()));
//            return checkPolicy(context);
        } catch (JSONException e) {
            e.printStackTrace();
//            return "服务器数据解析失败";
        }
    }


    //目前没用到，可删
    public static void getNewestPolicyByUser(String userAccount) {
        // TODO 获取用户名下的最新策略

    }

    /**
     * TODO
     * 检测本地策略是否为最新
     *
     * @return
     */
    public static boolean localPolicyIsNewest(Context context ,String deviceID) throws Exception{
        //每次同步策略时,将同步的服务器时间存储到sharedpreference中
        //从服务器请求最新的策略的时间，和本地存储的时间比较，若不同，则返回false
        Long oldPolicy = getPolicyLong(context, PREF_AUTH_POLICY_TIME, 0L);
        PropertyInfo[] property = new PropertyInfo[1];
        property[0] = new PropertyInfo();
        property[0].setName("deviceID");
        property[0].setValue(deviceID);
        property[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "getDevicePolicyTime", property);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            Long newPolicyTime = Long.parseLong(result==null? "0":result);
            return newPolicyTime.equals(oldPolicy);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }

    public static int getPolicyInt(Context cxt, String key, int defValue) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(cxt);
        return prefs.getInt(key, defValue);
    }

    public static String getPolicyString(Context cxt, String key, String defValue) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(cxt);
        return prefs.getString(key, defValue);
    }

    public static boolean getPolicyBool(Context cxt, String key, boolean defValue) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(cxt);
        return prefs.getBoolean(key, defValue);
    }

    public static Long getPolicyLong(Context cxt, String key, Long defValue) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(cxt);
        return prefs.getLong(key, defValue);
    }

    public static Long getLatestPolicyTime(Context cxt, Long defValue) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(cxt);
        return prefs.getLong(PREF_DEVICE_POLICY_TIME, defValue);
    }

    /**
     * delete all local policy data
     */
    public static void deleteLocalPolicy(Context ctx) {
        SharedPreferences prefs = CommonUtils.initSharedPreferences(ctx);
        prefs.edit().clear().commit();
    }


}
