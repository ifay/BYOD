package com.byod.application.perm;

import android.Manifest.permission;

public class PermCosntants {

    /*
     * 敏感权限定义：
     * 　IMEI(可伪造)
　　IMSI(可伪造)
　　SIM卡序列号(可伪造)
　　手机号码(可伪造)
　　来，去电号码
　　SIM卡信息
　　当前蜂窝网络信息
　　(以上七者均来自Android.Permission.READ_PHONE_STATE)

　　GPS定位信息 (可伪造，来自Android.Permission.FINE_LOCATION)

　　基站定位   (可伪造，来自Android.Permission.COARSE_LOCATION)

　　系统自带浏览器的历史，书签(Android.Permission.BOOKMARKS)

　　联系人    (android.permission.READ_CONTACTS)

　　通话记录   (android.permission.READ_CONTACTS)

　　系统日志   (android.permission.READ_LOGS)

　　当前账户列表   (android.permission.GET_ACCOUNTS)

　　当前账户的授权码  (android.permission.USE_CREDENTIALS)

　　短信，彩信 (可能与这5个权限有关)

　　　　　　　　　　android.permission.READ_SMS

　　　　　　　　　　android.permission.RECEIVE_SMS

　　　　　　　　　　android.permission.SEND_SMS

　　　　　　　　　　android.permission.WRITE_SMS

　　　　　　　　　　android.permission.RECEIVE_MMS

　　日历    android.permission.READ_CALENDAR
     */

    public static final String[] PERMS_SENSTIVE = {
        permission.READ_PHONE_STATE,
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION,
        permission.READ_CONTACTS,
        permission.READ_CALENDAR,
        permission.READ_SMS,
        permission.USE_CREDENTIALS,
        permission.SEND_SMS,
        permission.WRITE_SMS
        /*to be continued*/};

    public static final String[] PERMS_SMS = {
        permission.READ_SMS,
        permission.SEND_SMS,
        permission.WRITE_SMS};

    public static final String[] PERMS_CALL = {
        permission.CALL_PHONE,
        permission.CALL_PRIVILEGED};

//    public static final String[] PERMS_CALL_LOG = {
//        permission.READ_CALL_LOG,
//        permission.WRITE_CALL_LOG};

    public static final String[] PERMS_CONTACT = {
        permission.READ_CONTACTS,
        permission.WRITE_CONTACTS,
        permission.READ_PROFILE,
        permission.WRITE_PROFILE};

    public static final String[] PERMS_LOCATION = {
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION};

    public static final String[] PERMS_NETWORKS = {
        permission.ACCESS_NETWORK_STATE,
        permission.ACCESS_WIFI_STATE,
        permission.INTERNET};
}
