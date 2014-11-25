package com.byod.utils;

public class AuthUtils {

    /**
     * 登录，服务器端检测匹配度
     *
     * @param userAccount
     * @param pwd
     * @param deviceID
     * @return success，fail
     */
    public static boolean login(String userAccount, String pwd, String deviceID) {
        //TODO 
        return CommonUtils.SUCCESS;
    }

    /**
     * 向服务器查询用户与设备是否已绑定
     *
     * @param userAccount
     * @param deviceID
     * @return
     */
    public static boolean isUserAndDeviceBinded(String userAccount, String deviceID) {
        //TODO
        return CommonUtils.SUCCESS;
    }

    /**
     * 想userAccount绑定新设备
     * 调用服务器WS（服务器同时需要注册设备？）
     *
     * @param userAccount
     * @return
     */
    public static boolean addDeviceToUser(String userAccount) {
        // TODO 
        // 将context中的设备相关信息全部发送至服务器

        return CommonUtils.SUCCESS;

    }

    //根据用户的账号获得ID
    public static String getUserID(String userAccount) {
        return "2328";
    }
}
