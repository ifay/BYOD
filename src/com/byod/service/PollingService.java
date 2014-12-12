/**
 * 
 */

package com.byod.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.byod.PeerDeviceApproveActivity;
import com.byod.R;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;

/**
 * @author ifay
 */
public class PollingService extends Service {

    public static final String TAG = "PollingService";
    public static final String ACTION = CommonUtils.ACTION_POLL_SERVICE;

    private Notification mNotification;
    private NotificationManager mManager;
    
    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initNotifiManager();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        deviceID = intent.getExtras().getString("DeviceID");
//        Log.d(TAG, "getIntent deviceID is:"+deviceID);
        new PollingThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    // 初始化通知栏配置
    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "New Message";
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    // 弹出新设备注册Notification 
    private void showPeerDeviceNotification(String[] peerInfo) {
        mNotification.when = System.currentTimeMillis();
        // Navigator to the new activity when click the notification title
        Intent i = new Intent(this, PeerDeviceApproveActivity.class);
        i.putExtra("DeviceInfo", peerInfo);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        mNotification
                .setLatestEventInfo(this,
                        getResources().getString(R.string.app_name), "检测到新设备注册申请!",
                        pendingIntent);
        mManager.notify(0, mNotification);
    }

    /**
     * Polling thread 模拟向Server轮询的异步线程
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            //向server查询是否有peer device,返回devieID，deviceName | null
            String[] peerInfo = null;
            try {
                peerInfo = DeviceUtils.queryPeerDevices();
                if (peerInfo != null) {
                    showPeerDeviceNotification(peerInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
