package com.byod.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.stericson.RootTools.RootTools;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 
 * @author ifay
 * 单例使用
 * 提供设备唯一标识
 * 第一次使用，将设备ID记录到sharedPreference中??
 * 
 * 判断是否root
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

    public static DeviceUtils getInstance (Context context){
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
        BLUETOOTH_MAC = BluetoothAdapter.getDefaultAdapter().getAddress();
    }


    /**
     * MD5方法计算DeviceID
     * 产生32位的16进制数据，总长为
     */
    public String getsDeviceIdMD5() {
        String longID = IMEI +IMSI + TEL + WLAN_MAC + BLUETOOTH_MAC;

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        md5.update(longID.getBytes());
        byte[] md5Data = md5.digest();  //密文
        //密文转换为十六进制字符串
        sDeviceID = getString(md5Data);
        return sDeviceID;
    }

    /**
     * SHA1方法计算DeviceID
     */
    public String getsDeviceIdSHA1() {
        String longID = IMEI +IMSI + TEL + WLAN_MAC + BLUETOOTH_MAC;
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sha1.update(longID.getBytes());
        byte[] sha1Data = sha1.digest();
        sDeviceID = getString(sha1Data);
        return sDeviceID;
    }

    private String getString(byte[] b) {
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

    public boolean checkDeviceCompliance() {
        //***TODO*******
        //什么样的合规性文件？需要入参吗
        return false;
        
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

    /**
     * 获得伪ID
     * @return
     */
    public String getPseudoID() {
        return PseudoID;
    }

    /**
     * 判断是否root
     * @return
     */
    public static boolean isRooted() {
        return RootTools.isRootAvailable() && RootTools.isAccessGiven();
    }
}
