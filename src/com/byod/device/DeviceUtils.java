package com.byod.device;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceUtils {

    public static DeviceUtils sInstance = null;
    public static String IMEI = null;   //device id: 对于三星测试机正确，对于htc显式的是MEID
    public static String IMSI = null;
    public static String TEL = null;

    public static DeviceUtils getInstance (Context context){
        if (sInstance == null) {
            sInstance = new DeviceUtils(context);
        }
        return sInstance;
    }

    private DeviceUtils(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        //IMSI = tm.getSimSerialNumber();
        IMSI = tm.getSubscriberId();    //for GSM phone only TODO
        TEL = tm.getLine1Number();  //maybe null
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
}
