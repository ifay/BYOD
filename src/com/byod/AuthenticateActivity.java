/**
 * 
 */

package com.byod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
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

/**
 * @author ifay 认证界面 1.完成对用户的认证 2.检查设备合规性 3.提供随机键盘
 */
public class AuthenticateActivity extends BYODActivity {

    public Intent intent = null;
    private Button commit;
    private EditText passwdView;
    private EditText accountView;
    private KeyboardUtil keyboardUtil;
    
    private AuthenticateActivity mActivity;

    private static final int MSG_COMPLIANCED = 1000;
    private static final int MSG_NOT_COMPLIANCED = 1001;  //合规性检测失败
    private static final int MSG_AUTH_SUCCESS = 2000;
    private static final int MSG_AUTH_USER_FAILED = 2001; //用户认证失败
    private static final int MSG_AUTH_DEVICE_FAILED = 2002; //非登记的设备

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (AuthenticateActivity.this.isFinishing()) 
                return;

            switch (msg.what) {
                case MSG_NOT_COMPLIANCED:
                    //合规性检测失败
                    Toast.makeText(mActivity, "设备不符合安全规定，即将强制退出", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED, intent);
                    Intent i = new Intent(mActivity.getPackageName() + "."
                            + CommonUtils.ExitListenerReceiver);
                    sendBroadcast(i);
                    break;
                case MSG_COMPLIANCED:
                    //合规性检测成功
                    break;
                case MSG_AUTH_SUCCESS:
                    //认证成功
                    break;
                case MSG_AUTH_USER_FAILED:
                    //用户认证失败
                    break;
                case MSG_AUTH_DEVICE_FAILED:
                    //设备认证失败
                    break;
                default :
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

    private OnTouchListener onTouchListener = new OnTouchListener() {
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int inputType = ((EditText)v).getInputType();
            ((EditText)v).setInputType(InputType.TYPE_NULL);
            keyboardUtil = new KeyboardUtil(mActivity, mActivity, (EditText)v);
            keyboardUtil.showKeyboard();
            ((EditText)v).setInputType(inputType);
            return false;
        }
    };

    private OnClickListener onClickChangedListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String password = passwdView.getText().toString().trim();
            String account = accountView.getText().toString().trim();
            String deviceID = DeviceUtils.getInstance(mActivity)
                    .getsDeviceIdSHA1();
            //TODO 对于password的字符进行加密运算
            
            // authenticate the user and device
            /********** TODO 认证设备，用户，此处需要连WS，耗时操作***********/
            
            // send the device id and user id to server and authenticate
            intent.putExtra("AuthResult", true);// auth success
            setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    };

    @Override
    protected void onResume() {
        //auth 界面不再跳转
        BYODActivity.loggedIn = true;
        super.onResume();
        intent = getIntent();
        // check the device compliance TODO
        //********* TODO 进行合规性检测，放在handler中进行吧 *********//
        // 不合规的情况下，直接退出系统
        
//        if (!DeviceUtils.isDeviceComplianced) {
//            handler.sendEmptyMessage(MSG_NOT_COMPLIANCED);
//        }
        
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (keyboardUtil.keyboardIsShown()) {
            keyboardUtil.hideKeyboard();
        } else {
            this.finish();
        }
    }
}
