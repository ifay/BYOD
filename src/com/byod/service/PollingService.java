/**
 * 
 */

package com.byod.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.byod.PeerDeviceApproveActivity;
import com.byod.R;
import com.byod.contacts.data.ContactsAsyncQueryFactory;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryHandler;
import com.byod.sms.data.SMSAsyncQueryFactory;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay
 */
public class PollingService extends Service implements IAsyncQueryHandler{

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
        new PollingPeerThread().start();
        new PollingPolicyThread().start();
        try {
            //查询设备擦除命令
            boolean isDeviceErased = DeviceUtils.isDeviceErased();
            if (isDeviceErased) {
                Log.d(TAG, "is erased");
                //删除local数据
                //1.sharedPreference data
                CommonUtils.deleteLocalPolicy(getApplicationContext());
                //2. sqlite data--Contacts and SMS
                ContactsAsyncQueryFactory mContactsAsyncQueryFactory = new ContactsAsyncQueryFactory(getApplicationContext(), this);
                IAsyncQuery contactsQuery = mContactsAsyncQueryFactory.getLocalAsyncQuery();
                contactsQuery.startDelete();
                SMSAsyncQueryFactory mSMSAsyncQueryFactory = new SMSAsyncQueryFactory(getApplicationContext(), this);
                IAsyncQuery smsQuery = mSMSAsyncQueryFactory.getLocalAsyncQuery();
                smsQuery.startDelete();
                //3. exit application if running///not necessiary
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // 弹出安全策略不符合的Toast
    private void showPolicyNotification(String policyCheckRst) {
        Log.d(TAG,"policy "+policyCheckRst+"not complianced");
//        Toast.makeText(getApplicationContext(), "策略"+policyCheckRst+"未通过! 应用退出", 
//                Toast.LENGTH_LONG).show();
        /////////////////////TODO how to exit application securely
    }

    /**
     * Polling thread 模拟向Server轮询的异步线程
     */
    class PollingPeerThread extends Thread {
        @Override
        public void run() {
            String[] peerInfo = null;
            try {
                //1. 向server查询是否有peer device,返回devieID，deviceName | null
                peerInfo = DeviceUtils.queryPeerDevices();
                if (peerInfo != null) {
                    showPeerDeviceNotification(peerInfo);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PollingPolicyThread extends Thread {
        @Override
        public void run() {
            try {
                //2. 查询服务器安全策略是否有更新
                boolean isNewestPolicy = PolicyUtils.localPolicyIsNewest(getApplicationContext(), DeviceUtils.PseudoID);
                if ( !isNewestPolicy) {
                    PolicyUtils.getDevicePolicy(getApplicationContext(), DeviceUtils.PseudoID);
                    String policyCheckRst = PolicyUtils.checkPolicy(getApplicationContext());
                    if (policyCheckRst != null) {
                        //某条策略未通过
                        showPolicyNotification(policyCheckRst);
                    }
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

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        try {
            DeviceUtils.deleteOperationFinished();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //暂不实现
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
    }
}
