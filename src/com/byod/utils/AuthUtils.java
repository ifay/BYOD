package com.byod.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ksoap2.serialization.PropertyInfo;


public class AuthUtils {

    private static ExecutorService pool = Executors.newCachedThreadPool();
    /**
     * 登录，服务器端检测匹配度
     *
     * @param userAccount
     * @param pwd
     * @param deviceID
     * @return success，fail
     */
    public static boolean login(String userAccount, String pwd, String deviceID) {
        PropertyInfo[] properties = new PropertyInfo[3];
        properties[0] = new PropertyInfo();
        properties[0].setName("userAccount");
        properties[0].setValue(userAccount);
        properties[0].setType(PropertyInfo.STRING_CLASS);
        properties[1] = new PropertyInfo();
        properties[1].setName("pwd");
        properties[1].setValue(pwd);
        properties[1].setType(PropertyInfo.STRING_CLASS);
        properties[2] = new PropertyInfo();
        properties[2].setName("deviceID");
        properties[2].setValue(deviceID);
        properties[2].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "login2", properties);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result.equals("true");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return CommonUtils.FAIL;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return CommonUtils.FAIL;
        }
    }

    /**
     * 向服务器查询用户与设备是否已绑定
     *
     * @param userAccount
     * @param deviceID
     * @return
     * @throws Exception 
     */
    public static boolean isUserAndDeviceBinded(String userAccount, String deviceID) throws Exception {
        PropertyInfo[] properties = new PropertyInfo[2];
        properties[0] = new PropertyInfo();
        properties[0].setName("userAccount");
        properties[0].setValue(userAccount);
        properties[0].setType(PropertyInfo.STRING_CLASS);
        properties[1] = new PropertyInfo();
        properties[1].setName("deviceID");
        properties[1].setValue(deviceID);
        properties[1].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "isUserAndDeviceBinded", properties);
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
     * 想userAccount绑定新设备
     * 调用服务器WS（服务器同时需要注册设备？）
     *
     * @param userAccount
     * @return
     * @throws Exception 
     */
/*    public static boolean addDeviceToUser(String userAccount) throws Exception {
        // TODO 
        // 将context中的设备相关信息全部发送至服务器
        PropertyInfo[] properties = new PropertyInfo[2];
        properties[0] = new PropertyInfo();
        properties[0].setName("userAccount");
        properties[0].setValue(userAccount);
        properties[0].setType(PropertyInfo.STRING_CLASS);
        properties[1] = new PropertyInfo();
        properties[1].setName("deviceJson");
        properties[1].setValue(userAccount);//TODO  参数待定
        properties[1].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "deviceRegister", properties);
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
*/
    
    //根据用户的账号获得ID
    public static String getUserID(String userAccount) throws Exception  {
        PropertyInfo[] properties = new PropertyInfo[1];
        properties[0] = new PropertyInfo();
        properties[0].setName("userAccount");
        properties[0].setValue(userAccount);
        properties[0].setType(PropertyInfo.STRING_CLASS);
        WebConnectCallable task = new WebConnectCallable(
                CommonUtils.IAM_URL, CommonUtils.IAM_NAMESPACE, "getUserID", properties);
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        Future<String> future = pool.submit(task);
        try {
            String result = future.get();
            return result;
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }
}
