package com.byod;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.byod.bean.ContactBean;
import com.byod.sms.service.SMSObserverService;

import java.util.LinkedList;
import java.util.List;

public class BYODApplication extends Application {

    private static BYODApplication sInstance;
    public static String userAccount;
    public static boolean loggedIn = false;
    public static int REQUEST_AUTH_CODE = 1;
    private List<ContactBean> contactBeanList;

    // 对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList实现了基于动态数组的数据结构，要移动数据。LinkedList基于链表的数据结构,便于增加删除
    private List<Activity> activityList = new LinkedList<Activity>();

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    // 退出应用：清除缓存
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
    
    public static BYODApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        startService(new Intent(this, SMSObserverService.class));
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        loggedIn = false;
    }

    public List<ContactBean> getContactBeanList() {
        return contactBeanList;
    }

    public void setContactBeanList(List<ContactBean> contactBeanList) {
        this.contactBeanList = contactBeanList;
    }
}
