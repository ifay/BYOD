package com.byod.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

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

    private static String sDeviceID = null;

    private static DeviceUtils sInstance = null;
    //IMEI,IMSI 二选一
    private static String IMEI = null;   //device id: 对于三星测试机正确，对于htc显式的是MEID，15位
    private static String IMSI = null;
    private static String TEL = null;
    private static String WLAN_MAC = null;   //形如00:11:22:33:44:55，易被伪造
    private static String BLUETOOTH_MAC = null;  //仅支持有蓝牙的设备
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
        String longID = IMEI + IMSI + TEL + WLAN_MAC + BLUETOOTH_MAC;
        sDeviceID = CommonUtils.cryptSH1(longID);
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
     * @return 0：通过。正数：某条策略未通过
     */
    public static int isDeviceComplianced(Context context) {
        //1.sync the newest policy
        PolicyUtils.getNewestPolicy();
        SharedPreferences prefs = PolicyUtils.initSharedPreferences(context);

        //将设备信息发送至服务器，由服务器进行检测。

        return 0;

    }

    /**
     * 向服务器查询用户所有设备数目
     *
     * @param userAccount 用户账户唯一标识，此处用邮箱
     * @return 用户所绑定的设备数目
     */
    public static int getUserDeviceNum(String userAccount) {
        //TODO
        return 0;
    }

    /**
     * 判断是否root
     *
     * @return
     */
    public static boolean isRooted() {
        return RootTools.isRootAvailable() && RootTools.isAccessGiven();
    }
}
