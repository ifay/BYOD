/**
 * 
 */
package com.byod.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byod.service.PollingService;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.PolicyUtils;
import com.byod.utils.PollingUtils;

/**
 * @author ifay
 *
 */
public class BootReceiver extends BroadcastReceiver {
    private String deviceID = null;

    private static final String TAG = "BootReceiver";
    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent "+intent.getAction());
        //start service if not first install BYOD
        if (PolicyUtils.getLatestPolicyTime(context, 0L) != 0L) {
            deviceID = DeviceUtils.getInstance(context).getsDeviceIdSHA1();
            //start polling service
            Intent i = new Intent(context,PollingService.class);
            i.setAction(CommonUtils.ACTION_POLL_SERVICE);
            i.putExtra("DeviceID", deviceID);
            PollingUtils.startPollingService(context, CommonUtils.POLL_PEER, i);
        }
        //TODO start msg & phone listener
    }

}
