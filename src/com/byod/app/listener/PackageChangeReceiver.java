/**
 * 
 */
package com.byod.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author ifay
 * 监听应用安装卸载事件
 * 和黑白名单进行匹配
 */
public class PackageChangeReceiver extends BroadcastReceiver {

    public String packageName;

    public PackageChangeReceiver() {
    }

    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        packageName = intent.getDataString();
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            //install events
            //if packageName is in the black list, give a notification and exit.
            //TODO
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            //uninstall
            //check if the removed app is in the white list
            // if not, do nothing
            // if in, give a notification, and end app
            //TODO
        }
        
    }

}
