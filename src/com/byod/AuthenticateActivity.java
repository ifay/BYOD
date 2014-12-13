package com.byod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.byod.launcher.HomeScreen;
import com.byod.utils.AuthUtils;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.KeyboardUtil;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay 认证界面 1.完成对用户的认证 2.检查设备合规性 3.提供随机键盘
 * 1.检查设备合规性
 * 2.对用户认证
 * 3.随机软键盘
 */
public class AuthenticateActivity extends Activity {

    private String TAG = "AuthenticateActivity";

    public Intent intent = null;
    private Button commit;
    private Button getKeyboardBtn;
    private EditText passwdView;
    private EditText accountView;
    private KeyboardUtil keyboardUtil;
    private String keyboardJson;
    private static int sAuthFailTime = 0;

    private AuthenticateActivity mActivity;

    private static final int MSG_COMPLIANCED = 1000;
    private static final int MSG_NOT_COMPLIANCED = 1001; // 合规性检测失败
    private static final int MSG_AUTH_SUCCESS = 2000;
    private static final int MSG_AUTH_FAILED = 2001; // 认证失败
    private static final int MSG_AUTH_USER_DELETED = 2002;  //用户已注销

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (AuthenticateActivity.this.isFinishing())
                return;
            Intent i;
            switch (msg.what) {
                case MSG_NOT_COMPLIANCED:
                    // 合规性检测失败,退出应用
                    Bundle data = msg.getData();
                    int checkResult = data.getInt(PolicyUtils.POLICY_RESULT);
                    Toast.makeText(mActivity, "设备不符合安全规定" + checkResult +
                            "，应用即将退出", Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED, intent);
                    BYODApplication.getInstance().exit();
                    break;
                case MSG_COMPLIANCED:
                    // 合规性检测成功
                    commit.setEnabled(true);
                    commit.setOnClickListener(login);
                    break;
                case MSG_AUTH_SUCCESS:
                    // 认证成功
                    Log.d(TAG, "MSG_AUTH_SUCCESS");
                    BYODApplication.loggedIn = true;
                    i = new Intent(mActivity, HomeScreen.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;
                case MSG_AUTH_FAILED:
                    // 用户认证失败
                    Log.d(TAG, "MSG_AUTH_USER_FAILED");
                    sAuthFailTime += 1;
                    popLockUserDialog();
                    break;
                case MSG_AUTH_USER_DELETED:
                    // 用户已删除
                    Log.d(TAG, "MSG_AUTH_USER_DELETED");
                    Toast.makeText(mActivity, "用户已注销,应用退出", Toast.LENGTH_LONG).show();
                    BYODApplication.getInstance().exit();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.authenticate);
        this.mActivity = this;
        BYODApplication.getInstance().addActivity(this);
        commit = (Button) findViewById(R.id.commit);
        getKeyboardBtn = (Button) findViewById(R.id.getKeyboard);
        passwdView = (EditText) findViewById(R.id.passwd);
        accountView = (EditText) findViewById(R.id.account);
        passwdView.setOnTouchListener(onTouchListener);
        commit.setEnabled(false);
        commit.setOnClickListener(login);
        getKeyboardBtn.setOnClickListener(getKeyboard); 
    }

    //【获取键盘】
    private OnClickListener getKeyboard = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            String userAccount = accountView.getText().toString();
            if (userAccount == null || userAccount.length() < 1) {
                Toast.makeText(mActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
                return ;
            }
            
            //1. new keyboard
            if (keyboardUtil == null) {
                keyboardUtil = new KeyboardUtil(mActivity, mActivity, passwdView, R.id.keyboard_view);
            }
            
            //2. get keyboard from server
            try {
                keyboardJson = keyboardUtil.getRandomKeyboard(userAccount.trim());
                if (keyboardJson.length() < 1) {
                    Toast.makeText(mActivity, "键盘获取失败，请重试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    v.setVisibility(View.GONE);
                    //3. show password edittext
                    passwdView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Toast.makeText(mActivity, "键盘获取失败，请重试", Toast.LENGTH_SHORT).show();
                return ;
            }
        }
    };
    
    // 【密码框】EditText
    private OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int inputType = ((EditText) v).getInputType();
            ((EditText) v).setInputType(InputType.TYPE_NULL);

            if (keyboardUtil == null) {
                keyboardUtil = new KeyboardUtil(mActivity, mActivity, (EditText) v, R.id.keyboard_view);
            }
            String userAccount = accountView.getText().toString();
            if (userAccount == null || userAccount.length() < 1) {
                Toast.makeText(mActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                keyboardUtil.showRandomKeyboard(keyboardJson);
                keyboardUtil.showKeyboard();
            } catch (Exception e) {
                Toast.makeText(mActivity, "键盘加载失败，请重试", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            
            ((EditText)v).setInputType(inputType);
            return false;
            
        }
    };

    //【登录】 submit button
    private OnClickListener login = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final String password = passwdView.getText().toString().trim();
            final String account = accountView.getText().toString().trim();

            if (password.length() < 1) {
                Toast.makeText(mActivity, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            final String deviceID = DeviceUtils.getInstance(mActivity).getsDeviceIdSHA1();

            //2. authenticate the user and device
            int authResult = AuthUtils.login(account, password, deviceID);
            if (authResult == 1) {
                CommonUtils.setPrefString(mActivity, CommonUtils.PREF_KEY_USERACCOUNT, account);
                handler.sendEmptyMessage(MSG_AUTH_SUCCESS);
            } else if (authResult == 0) {
                handler.sendEmptyMessage(MSG_AUTH_FAILED);
            }
            else {
                handler.sendEmptyMessage(MSG_AUTH_USER_DELETED);
            }
        }
    };



    /**
     * 登录次数超过允许值时，弹出提示窗口，确认后退出应用，服务器将锁定账户及设备
     */
    private void popLockUserDialog() {
        Log.d(TAG, "popLockUserDialog");

        int maxAuthTime = PolicyUtils.getPolicyInt(mActivity, PolicyUtils.PREF_PWD_TIRAL_TIME, 4);
        if (sAuthFailTime > maxAuthTime) {
            Log.d("trial time", sAuthFailTime + "");
            AlertDialog.Builder dialog = new Builder(mActivity);
            dialog.setTitle("登录错误");
            dialog.setMessage("登录次数超过" + maxAuthTime + "次\n应用将关闭");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(Activity.RESULT_CANCELED, intent);
                    BYODApplication.getInstance().exit();
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        // auth 界面不再跳转
        super.onResume();
        intent = getIntent();

        // 2. check the device compliance
        String rst;
        try {
            rst = DeviceUtils.isDeviceComplianced(mActivity);
            if (rst == null) {
                handler.sendEmptyMessage(MSG_COMPLIANCED);
                //直接进行后续操作
            } else {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString(PolicyUtils.POLICY_RESULT, rst);
                msg.setData(data);
                msg.what = MSG_NOT_COMPLIANCED;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        // clear Keyboard first
        if (keyboardUtil != null && keyboardUtil.keyboardIsShown()) {
            Log.d(TAG , "hide keyboard");
            keyboardUtil.hideKeyboard();
            keyboardUtil = null;
        }
        super.onBackPressed();
    }
    
    
}
