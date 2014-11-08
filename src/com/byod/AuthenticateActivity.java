package com.byod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.KeyboardUtil;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay 认证界面 1.完成对用户的认证 2.检查设备合规性 3.提供随机键盘
 */
public class AuthenticateActivity extends BYODActivity {

    public Intent intent = null;
    private Button commit;
    private EditText passwdView;
    private EditText accountView;
    private KeyboardUtil keyboardUtil;
    private static int sAuthFailTime = 0;

    private AuthenticateActivity mActivity;

    private DevicePolicyManager dpManager = null;

    private static final int MSG_COMPLIANCED = 1000;
    private static final int MSG_NOT_COMPLIANCED = 1001; // 合规性检测失败
    private static final int MSG_NOT_ADMIN = 1002; // 未开启设备管理器
    private static final int MSG_AUTH_SUCCESS = 2000;
    private static final int MSG_AUTH_USER_FAILED = 2001; // 用户认证失败
    private static final int MSG_AUTH_DEVICE_FAILED = 2002; // 非登记的设备

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (AuthenticateActivity.this.isFinishing())
                return;
            Intent i;
            switch (msg.what) {
                case MSG_NOT_COMPLIANCED:
                    // 合规性检测失败,退出应用
                    Toast.makeText(mActivity, "设备不符合安全规定，应用即将退出", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED, intent);
                    i = new Intent(mActivity.getPackageName() + "."
                            + CommonUtils.ExitListenerReceiver);
                    sendBroadcast(i);
                    break;
                case MSG_NOT_ADMIN:
                    // 合规性检测失败,退出应用
                    Toast.makeText(mActivity, "未开启设备管理器功能，应用即将退出", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED, intent);
                    i = new Intent(mActivity.getPackageName() + "."
                            + CommonUtils.ExitListenerReceiver);
                    sendBroadcast(i);
                    break;
                case MSG_COMPLIANCED:
                    // 合规性检测成功
                    Log.d("AuthenticateActivity", "MSG_COMPLIANCED");
                    break;
                case MSG_AUTH_SUCCESS:
                    // 认证成功
                    Log.d("AuthenticateActivity", "MSG_AUTH_SUCCESS");
                    
                    break;
                case MSG_AUTH_USER_FAILED:
                    // 用户认证失败
                    Log.d("AuthenticateActivity", "MSG_AUTH_USER_FAILED");
                    sAuthFailTime += 1;
                    popLockUserDialog();
                    break;
                case MSG_AUTH_DEVICE_FAILED:
                    // 设备认证失败
                    Log.d("AuthenticateActivity", "MSG_AUTH_DEVICE_FAILED");
                    sAuthFailTime += 1;
                    popLockUserDialog();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onCreate() {
        setContentView(R.layout.authenticate);
        this.mActivity = this;
        commit = (Button) findViewById(R.id.commit);
        passwdView = (EditText) findViewById(R.id.passwd);
        accountView = (EditText) findViewById(R.id.account);
        passwdView.setOnTouchListener(onTouchListener);
        accountView.setOnTouchListener(onTouchListener);
        commit.setOnClickListener(onClickChangedListener);
    }

    //EditText
    private OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int inputType = ((EditText) v).getInputType();
            ((EditText) v).setInputType(InputType.TYPE_NULL);
            keyboardUtil = new KeyboardUtil(mActivity, mActivity, (EditText) v);
            keyboardUtil.showKeyboard();
            ((EditText) v).setInputType(inputType);
            return false;
        }
    };

    //submit button
    private OnClickListener onClickChangedListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            //1.检查是否激活Device admin
            if(!PolicyUtils.isAdminActive(mActivity)) {
                handler.sendEmptyMessage(MSG_NOT_ADMIN);
            }
            
            //2. 检查设备合规性
//            handler.sendEmptyMessage(MSG_COMPLIANCED);
//            handler.sendEmptyMessage(MSG_NOT_COMPLIANCED);

            //3.TODO 对于password的字符进行加密运算
            String password = passwdView.getText().toString().trim();
            String account = accountView.getText().toString().trim();
            String deviceID = DeviceUtils.getInstance(mActivity)
                    .getsDeviceIdSHA1();
            
            //4.authenticate the user and device
            /********** TODO 认证设备，用户，此处需要连WS，耗时操作 ***********/
            // send the device id and user id to server and authenticate
//            handler.sendEmptyMessage(MSG_AUTH_SUCCESS);
            handler.sendEmptyMessage(MSG_AUTH_USER_FAILED);
        }
    };

    /**
     * 登录次数超过允许值时，弹出提示窗口，确认后退出应用，服务器将锁定账户及设备
     */
    private void popLockUserDialog() {
        if (sAuthFailTime > 0) {
            Log.d("trial time", sAuthFailTime+"");
            AlertDialog.Builder dialog = new Builder(mActivity);
            dialog.setTitle("登录错误");
            dialog.setMessage("登录次数超过"+PolicyUtils.sAuthMaxTime+"次\n应用将关闭");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(Activity.RESULT_CANCELED, intent);
                   Intent i = new Intent(mActivity.getPackageName() + "."
                            + CommonUtils.ExitListenerReceiver);
                    sendBroadcast(i);
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        // auth 界面不再跳转
        BYODApplication.loggedIn = true;
        super.onResume();
        intent = getIntent();

        //1.检测是否开启Device admin
        if (!PolicyUtils.isAdminActive(mActivity)) {
            PolicyUtils.ActivateDeviceAdmin(mActivity);
        }
        
        // check the device compliance TODO
        
        
        // ********* TODO 进行合规性检测，放在handler中进行吧 *********//
        // 不合规的情况下，直接退出系统

        // if (!DeviceUtils.isDeviceComplianced) {
        // handler.sendEmptyMessage(MSG_NOT_COMPLIANCED);
        // }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //clear Keyboard first
        if (keyboardUtil.keyboardIsShown()) {
            keyboardUtil.hideKeyboard();
        } else {
            this.finish();
        }
    }
    
}
