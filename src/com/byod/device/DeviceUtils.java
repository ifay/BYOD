package com.byod.device;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceUtils {

    public static DeviceUtils sInstance = null;
    public static String sDeviceID = null;  //IMEI ***  TelephonyManager.getDeviceId()

    public static DeviceUtils getInstance (Context context){
        if (sInstance == null) {
            sInstance = new DeviceUtils(context);
        }
        return sInstance;
    }

    private DeviceUtils(Context context) {
        // TODO init works
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        sDeviceID = tm.getDeviceId();
    }

    /*
     * notice: android.permission.READ_PHONE_STATE 
     */
    public static String getDeviceID() {
        return sDeviceID;
    }
}
