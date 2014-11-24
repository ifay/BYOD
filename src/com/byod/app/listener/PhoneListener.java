/**
 *
 */
package com.byod.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author ifay
 *         监听来电状态
 *         若为公司相关来电，则清除系统中的通话记录，并且转存在工作区
 *         TODO Manifest中注册
 */
public class PhoneListener extends BroadcastReceiver {

    public PhoneListener() {
    }

    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {

    }

}
