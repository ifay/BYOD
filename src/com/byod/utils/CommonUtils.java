/**
 * 
 */
package com.byod.utils;

import android.content.Context;
import android.content.Intent;


/**
 * @author ifay
 *
 */
public class CommonUtils {

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
    
    //通用代码
    public static boolean SUCCESS = true;
    public static boolean FAIL = false;
    
    public static void exitBYOD(Context context) {
        Intent intent;
        intent = new Intent(context.getPackageName() + "."
                + CommonUtils.ExitListenerReceiver);
        context.sendBroadcast(intent);
    }

}
