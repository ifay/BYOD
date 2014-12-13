/**
 * 
 */
package com.byod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.byod.application.DeviceRegisterActivity;
import com.byod.utils.DeviceUtils;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay
 *
 */
public class WelcomeActivity extends Activity {

    private TextView checkAdmin = null;
    private TextView checkDevReg = null;
    private TextView checkDevLock = null;
    private TextView checkPolicySync = null;
    private TextView checkPolicy = null;
    private Button welcomeResultBtn = null;
    private static final int MSG_ADMIN_OK = 1000;
    private static final int MSG_ADMIN_NOT_OK = 1001;
    private static final int MSG_DEV_REGISTERED = 2000;
    private static final int MSG_DEV_NOT_REGISTERED = 2001;
    private static final int MSG_DEV_NOT_LOCK = 3000;
    private static final int MSG_DEV_LOCK = 3001;
    private static final int MSG_POLICY_SYNC_OK = 4000;
    private static final int MSG_POLICY_SYNC_FAILED = 4001;
    private static final int MSG_POLICY_CHECKED = 5000;
    private static final int MSG_POLICY_CHECK_FAILED = 5001;
    private static final String POLICY_FAIL_REASON = "policy_fail_reason";

    private Context ctx;
    private static String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_check);
        BYODApplication.getInstance().addActivity(this);
        ctx = this;
        initView();
    }

    private void initView() {
        checkAdmin = (TextView) findViewById(R.id.checkAdmin);
        checkDevReg = (TextView) findViewById(R.id.checkDevReg);
        checkDevLock = (TextView) findViewById(R.id.checkDevLock);
        checkPolicySync = (TextView) findViewById(R.id.policySync);
        checkPolicy = (TextView) findViewById(R.id.checkPolicy);
        welcomeResultBtn = (Button) findViewById(R.id.welcomeResult);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch(code) {
                case MSG_ADMIN_OK:
                    checkAdmin.setText(R.string.checked);
                    //2.check register
                    checkRegistered();
                    break;
                case MSG_ADMIN_NOT_OK:
                    checkAdmin.setText(R.string.check_failed);
                    PolicyUtils.activateDeviceAdmin(ctx);
                    break;
                case MSG_DEV_REGISTERED:
                    checkDevReg.setText(R.string.checked);
                    //3.check server whether device is locked. use thread TODO
                    checkDeviceLocked();
                    break;
                case MSG_DEV_NOT_REGISTERED:
                    checkDevReg.setText(R.string.check_failed);
                    welcomeResultBtn.setText("注册设备");
                    welcomeResultBtn.setVisibility(View.VISIBLE);
                    welcomeResultBtn.setOnClickListener(register);
                    break;
                case MSG_DEV_NOT_LOCK:
                    checkDevLock.setText(R.string.checked);
                    checkPolicySync();
                    break;
                case MSG_DEV_LOCK:
                    checkDevLock.setText(R.string.check_failed);
                    welcomeResultBtn.setText("请联系管理员解锁并退出");
                    welcomeResultBtn.setVisibility(View.VISIBLE);
                    welcomeResultBtn.setOnClickListener(exit);
                    break;
                case MSG_POLICY_SYNC_OK:
                    checkPolicySync.setText(R.string.checked);
                    //5.check policy
                    checkPolicyResult();
                    break;
                case MSG_POLICY_SYNC_FAILED:
                    checkPolicySync.setText(R.string.check_failed);
                    welcomeResultBtn.setText("请检查网络连接并重试");
                    welcomeResultBtn.setVisibility(View.VISIBLE);
                    welcomeResultBtn.setOnClickListener(retry);
                    break;
                case MSG_POLICY_CHECKED:
                    checkPolicy.setText(R.string.checked);
                    welcomeResultBtn.setVisibility(View.VISIBLE);
                    welcomeResultBtn.setOnClickListener(enter);
                    break;
                case MSG_POLICY_CHECK_FAILED:
                    checkPolicy.setText(R.string.check_failed);
                    Bundle data = msg.getData();
                    String result = data.getString(POLICY_FAIL_REASON);
                    welcomeResultBtn.setText("策略"+result+"检测未通过\n点击退出");
                    welcomeResultBtn.setVisibility(View.VISIBLE);
                    welcomeResultBtn.setOnClickListener(exit);
                    break;
                default:
                    break;
            }
        }
    };
    
    //【进入】进入登录页面
    private OnClickListener enter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ctx, AuthenticateActivity.class);
            startActivity(i);
        }
    };

    //【注册设备】进入注册页面
    private OnClickListener register = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ctx,DeviceRegisterActivity.class);
            startActivity(i);
        }
    };

    //【退出】
    private OnClickListener exit = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            BYODApplication.getInstance().exit();
        }
    };

    //【检查网络并重试】重新同步策略
    private OnClickListener retry = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            checkPolicySync();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //1. check admin
        if (!PolicyUtils.isAdminActive(ctx)) {
            handler.sendEmptyMessage(MSG_ADMIN_NOT_OK);
        } else {
            handler.sendEmptyMessage(MSG_ADMIN_OK);
        }
    }
    

    /**
     * check device Register
     * 如果本地有策略，则说明之前使用过，即注册过
     */
    private boolean checkRegistered(){
        boolean isDeviceRegistered = false;
        try {
            isDeviceRegistered = DeviceUtils.getInstance(ctx).isDeviceRegistered();
            if (isDeviceRegistered) {
                handler.sendEmptyMessage(MSG_DEV_REGISTERED);
            } else {
                handler.sendEmptyMessage(MSG_DEV_NOT_REGISTERED);////////// TODO MSG_DEV_NOT_REGISTERED
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeviceRegistered;
    }

    /**
     * check device locked
     */
    private boolean checkDeviceLocked(){
        boolean isLocked = true;
        try {
            isLocked = DeviceUtils.getInstance(ctx).isDeviceLocked();
            if (isLocked == false) {
                handler.sendEmptyMessage(MSG_DEV_NOT_LOCK);
            } else {
                handler.sendEmptyMessage(MSG_DEV_LOCK);///////MSG_DEV_LOCK
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(MSG_DEV_REGISTERED);
        }
        return isLocked;
    }

    /**
     * sync the latest policy
     */
    private void checkPolicySync() {
        try {
            PolicyUtils.getDevicePolicy(ctx, DeviceUtils.getInstance(ctx).getsDeviceIdSHA1());
            handler.sendEmptyMessage(MSG_POLICY_SYNC_OK);
        } catch (Exception e) {
            handler.sendEmptyMessage(MSG_POLICY_SYNC_FAILED);////////MSG_POLICY_SYNC_FAILED
            e.printStackTrace();
        }
    }
    
    /**
     * 执行设备策略检查 
     * @return Policy Code
     */
    private void checkPolicyResult() {
        Log.d(TAG,"checkpolicyResult");
        String policyCode;
        try {
            policyCode = DeviceUtils.isDeviceComplianced(ctx);
            if(policyCode == null) {
                handler.sendEmptyMessage(MSG_POLICY_CHECKED);
            } else {
                ///////////TODO unComment below
                Log.d(TAG,"policCode:"+policyCode);
                Message msg = new Message();
                msg.what = MSG_POLICY_CHECK_FAILED;
                Bundle data = new Bundle();
                data.putString(POLICY_FAIL_REASON, policyCode);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BYODApplication.getInstance().removeActivity(this);
    }
}
