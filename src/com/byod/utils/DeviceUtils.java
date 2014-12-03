package com.byod.utils;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONStringer;
import org.ksoap2.serialization.PropertyInfo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.stericson.RootTools.RootTools;

/**
 * @author ifay
 *         单例使用
 *         <p/>
 *         提供设备唯一标识
 *         提供设备基本信息：OS，Version，Name
 *         第一次使用，将设备ID记录到sharedPreference中??
 *         判断是否root
 */
public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    private static ExecutorService pool = Executors.newCachedThreadPool();
    
    private static String sDeviceID = null;

    private static DeviceUtils sInstance = null;
    //IMEI,IMSI 二选一
    private static String IMEI = null;   //device id: 对于三星测试机正确，对于htc显式的是MEID，15位
    private static String IMSI = null;
    private static String TEL = null;
    private static String WLAN_MAC = null;   //形如00:11:22:33:44:55，易被伪造
    private static String BLUETOOTH_MAC = null;  //仅支持有蓝牙的设备
    private static String sDeviceManufacturer = null;
    private static String sDeviceOS = Build.VERSION.RELEASE;

    
    //根据ROM版本、制造商、CPU等硬件信息制作的伪ID，相同的硬件及ROM镜像时
    public static String PseudoID = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
            Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
            + Build.HOST.length() % 10 + Build.ID.length() % 10
            + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length()
            % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;

    public static boolean isDeviceComplianced = false;

    public static DeviceUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DeviceUtils(context);
        }
        return sInstance;
    }

    private DeviceUtils(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        IMEI = tm.getDeviceId();
        IMSI = tm.getSubscriberId();    //for GSM phone only TODO
        TEL = tm.getLine1Number();  //maybe null
        WLAN_MAC = wm.getConnectionInfo().getMacAddress();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != bluetoothAdapter) {
            BLUETOOTH_MAC = bluetoothAdapter.getAddress();
        }
        sDeviceManufacturer = Build.MANUFACTURER.toLowerCase();
        Log.d(TAG, "sDeviceManufacturer:"+sDeviceManufacturer);
    }


    /**
     * MD5方法计算DeviceID
     * 产生32位的16进制数据，总长为
     */
    public String getsDeviceIdMD5() {
        String longID = IMEI + IMSI + TEL + WLAN_MAC + BLUETOOTH_MAC;
        sDeviceID = CommonUtils.cryptMD5(longID);
        return sDeviceID;
    }

    /**
     * SHA1方法计算DeviceID
     */
    public String getsDeviceIdSHA1() {
        if (sDeviceID == null || sDeviceID.length() < 1) { 
            Log.d(TAG,"IMEI"+IMEI);
            Log.d(TAG,"IMSI"+IMSI);
            Log.d(TAG,"TEL"+TEL);
            Log.d(TAG,"WLAN_MAC"+WLAN_MAC);
            Log.d(TAG,"BLUETOOTH_MAC"+BLUETOOTH_MAC);
            String longID = IMEI + IMSI + TEL + WLAN_MAC + BLUETOOTH_MAC;
            longID.replaceAll(":", "");
            Log.d(TAG,"longID"+longID);
            sDeviceID = CommonUtils.cryptSH1(longID);
        }
        return sDeviceID;
    }

    public String getIMEI() {
        return IMEI;
    }


    public String getIMSI() {
        return IMSI;
    }

    public String getTEL() {
        return TEL;
    }

    public String getWLAN_MAC() {
        return WLAN_MAC;
    }

    public String getBLUETOOTH_MAC() {
        return BLUETOOTH_MAC;
    }

    public String getPseudoID() {
        return PseudoID;
    }

    /**
     * 对设备进行合规性检测
     * 将设备信息发送至服务器，由服务器进行检测
     *
     * @param context
     * @return null通过。"字符串"-未通过某条策略
     * @throws Exception 
     */
    public static String isDeviceComplianced(Context context) throws Exception {
        return PolicyUtils.checkPolicy(context);
    }

    /**
     * 向服务器查询用户所有设备数目 
     * @param userAccount 用户账户唯一标识，此处用邮箱
     * @return 用户所绑定的设备数目. -1表示密码错误
     * @throws Exception 
     */
    public static int getUserDeviceNum(String userAccount, String userPwd) throws Exception {
        PropertyInfo[] property = new PropertyInfo[2];
        property[0].setName("userAccount");
        property[0].setValue(userAccount);
        property[0].setType(PropertyInfo.STRING_CLASS);
        property[1].setName("pwd");
        property[1].setValue(userPwd);
        property[1].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "getUserDeviceNum", property);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        } 
        Future<String> future = pool.submit(task);
        String result;
        try {
            result = future.get();
            return Integer.parseInt(result);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }

    /**
     * 判断是否root
     *
     * @return
     */
    public static boolean isRooted() {
        return RootTools.isRootAvailable() && RootTools.isAccessGiven();
    }

    /**
     * 检测是否锁定
     * @return false:not locked; true:locked
     * @throws InterruptedException 
     * @throws ExecutionException 
     */
    public boolean isDeviceLocked() throws Exception {
        if (sDeviceID == null || sDeviceID.length() < 1) {
            getsDeviceIdSHA1();
        }
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceID");
        propertyInfo[0].setValue(sDeviceID);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isDeviceLocked", propertyInfo);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }

    /**
     * 新设备注册前需要由其他已注册的设备同意
     * 在registerDevice（context,false）的时候就已经完成
     */
    public boolean sendRegReqToPeerDevice() {
        return CommonUtils.SUCCESS;
    }
    
    /**
     * 向服务器查询设备注册请求是否已被批准
     * @return
     * @throws Exception 
     */
    public boolean isRegReqApproved() throws Exception {
        //send req to server, select by deviceiD
        String deviceID = getsDeviceIdSHA1();
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceID");////////TODO 用JSON格式
        propertyInfo[0].setValue(deviceID);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isDeviceActive", propertyInfo);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }
    
    private JSONStringer generateDeviceJSON (Context context, boolean isFirst) {
        LocationUtils location = new LocationUtils(context);
        JSONStringer deviceJson = new JSONStringer();
        try {
            deviceJson.object();
            deviceJson.key("deviceID");
            deviceJson.value(sDeviceID);
            deviceJson.key("userAccount");
            deviceJson.value(CommonUtils.getPrefString(context, CommonUtils.PREF_KEY_USERACCOUNT, "admin"));
            deviceJson.key("deviceName");
            deviceJson.value(sDeviceManufacturer);
            deviceJson.key("deviceOS");
            deviceJson.value(2000);//Android
            deviceJson.key("deviceMAC");
            deviceJson.value(WLAN_MAC);
            deviceJson.key("devicePhoneNum");
            deviceJson.value(TEL);
            deviceJson.key("deviceIsLock");
            deviceJson.value(0);
            deviceJson.key("deviceIsDel");
            deviceJson.value(0);
            deviceJson.key("deviceInitTime");
            deviceJson.value(new Date(System.currentTimeMillis())); //TODO 日期格式 long型
            deviceJson.key("deviceValidPeriod");
            deviceJson.value(365);////TODO 暂定365天有效。服务器需要支持修改
            deviceJson.key("deviceIsIllegal");
            deviceJson.value(0);
            deviceJson.key("deviceIsActive");
            deviceJson.value(isFirst? 1: 0);
            deviceJson.key("deviceIsLogout");
            deviceJson.value(0);
            deviceJson.key("deviceEaster");
            deviceJson.value(CommonUtils.getPrefString(context, CommonUtils.PREF_KEY_USERACCOUNT, "admin"));
            deviceJson.key("bakStr1");
            deviceJson.value(location.getLocation());//TODO 目前是经度+空格+纬度
            deviceJson.endObject();
            Log.d(TAG,deviceJson.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return deviceJson;
    }
    /**
     * 注册设备 , 同时发送Register request
     * @return
     * @throws Exception 
     */
    public boolean registerDevice(Context context, boolean isFirst) throws Exception {
        //send device info to the server
        String deviceJson = generateDeviceJSON(context,isFirst).toString();
        if (deviceJson.length() == 0) { //Device info generate fail
            return CommonUtils.FAIL;
        }
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceJSON");////////TODO 用JSON格式
        propertyInfo[0].setValue(deviceJson);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isDeviceLocked", propertyInfo);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }
    
    /**
     * TODO
     * 根据deviceID查询对应的userID下的 isActive为false的设备信息
     * @param deviceID
     * @return
     * @throws Exception 
     */
    public static String[] queryPeerDevices(String deviceID) throws Exception {
        // TODO query server use thread
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceID");
        propertyInfo[0].setValue(sDeviceID);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isDeviceLocked", propertyInfo);/////method change TODO
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
//            return result;
            //TODO 如何返回 设备信息呢？ 用JSON？///////////
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
        return new String[]{
                "deviceID","deviceName"
        };
    }

    /**
     * 允许设备注册
     * 将isActive置为true
     * @param deviceID
     * @throws Exception 
     */
    public static boolean approveDevice(String deviceID) throws Exception {
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceID");
        propertyInfo[0].setValue(sDeviceID);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "setDeviceActive", propertyInfo);/////method change TODO
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
        
    }

    /**
     * 查询设备注册是否被批准
     * @return 
     * @throws Exception
     */
    public boolean checkRegRequestApproved() throws Exception {
        String deviceID = getsDeviceIdSHA1();
        PropertyInfo[] propertyInfo = new PropertyInfo[1];
        propertyInfo[0].setName("deviceID");
        propertyInfo[0].setValue(sDeviceID);
        propertyInfo[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isDeviceActive", propertyInfo);/////method change TODO
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }
    
    /**
     * 不允许设备注册
     * delete device 对应的userID字段
     * @param deviceID
     */
    public static boolean disapproveDevice(String deviceID) {
        // TODO do nothing? or setDeviceIsDeleted?
        return CommonUtils.SUCCESS;
        
    }

    public static int getAPIVersion() {
        Log.d(TAG,"API version:"+Build.VERSION.SDK_INT);
        return Build.VERSION.SDK_INT;
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static boolean isGPRSEnabled(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
    }

    public static boolean isBlueToothEnabled(Context context) {
        // TODO Auto-generated method stub
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectMgr.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).isAvailable();
    }

    
    
 
}
