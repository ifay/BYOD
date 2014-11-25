package com.byod.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.R;
import com.byod.launcher.HomeScreen;
import com.byod.utils.AuthUtils;
import com.byod.utils.CommonUtils;
import com.byod.utils.DeviceUtils;
import com.byod.utils.KeyboardUtil;
import com.byod.utils.PolicyUtils;

/**
 * @author ifay 完善注册页面，（调用条件：首次使用设备登录） 尝试web View方式实现
 *         步骤一（服务器端）：管理员在UIA上录入用户的基本信息，用户获得默认权限-最低的权限
 *         步骤二：设备上没有策略信息：1-2-3（新用户） 或者 1-4（新设备）
 *         Page1: 输入用户名(可考虑做成翻页形式，“下一步”)
 *         Page2: 输入初始密码
 *         Page3：完善用户信息 （包括挑战问题）
 *         Page4：输入用户密码并添加设备
 *         考虑把登陆INFO的方法做成服务？？还是直接用WebView展示？
 */

public class UserRegisterPage1 extends Activity {

    private static final String LOG_TAG = "UserRegisterPage1";

    private static final int MSG_FIRST_DEVICE = 1000;   // 用户名下没有设备
    private static final int MSG_ADD_DEVICE = 1001;

    private UserRegisterPage1 mActivity;
    private EditText userAccountET = null;
    private EditText pwdET = null;
    private TextView textView2 = null;
    private Button nextBt = null;
    private static KeyboardUtil keyboard1 = null;
    private static KeyboardUtil keyboard2 = null;
    private String userAccount = "";
    private String userPwd = "";
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Intent i;
            switch (what) {
                case MSG_ADD_DEVICE:
                    // 显示密码框
                    // 弹出对话框提示是否添加
                    pwdET.setVisibility(View.VISIBLE);
                    pwdET.setOnTouchListener(showKeyboard);
                    textView2.setVisibility(View.VISIBLE);
                    nextBt.setOnClickListener(authAndAddDevice);
                    break;
                case MSG_FIRST_DEVICE:
                    // 显示密码框
                    // btn作用为：认证并跳转到完善信息页面
                    pwdET.setVisibility(View.VISIBLE);
                    pwdET.setOnTouchListener(showKeyboard);
                    textView2.setVisibility(View.VISIBLE);
                    nextBt.setText("增添此设备并下一步");
                    nextBt.setOnClickListener(authAndJumpListener);
                default:
                    break;
            }
        }
    };

    private void addNewDeviceDialog() {
        AlertDialog.Builder builder = new Builder(mActivity);
        builder.setTitle("新增设备");
        builder.setMessage("是否添加此设备为您名下的使用设备？");
        builder.setPositiveButton("添加", new OnClickListener() {
            Intent intent;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //注册设备
                        boolean result = AuthUtils.addDeviceToUser(userAccount);
                        if (result == CommonUtils.SUCCESS) {
                            //返回登录
                            setResult(Activity.RESULT_OK, mActivity.getIntent());
                            mActivity.finish();
                        } else {
                            Toast.makeText(mActivity, "增添设备失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                t.start();
            }
        });
        //不注册的话，该设备不能登录，直接退出
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommonUtils.exitBYOD(mActivity);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister1);
        mActivity = this;
        initView();
    }

    //按钮功能：获取用户名下设备,并进行合规性检测
    View.OnClickListener checkUserDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userAccount = userAccountET.getText().toString();
            if (userAccount.length() == 0) {
                Toast.makeText(mActivity, "用户名为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    int deviceNum = DeviceUtils.getUserDeviceNum(userAccount); // TODO fake data
                    int complianced = DeviceUtils.isDeviceComplianced(mActivity);
                    if (complianced == PolicyUtils.CODE_COMPLIANCED) {
                        if (deviceNum == 0) {
                            handler.sendEmptyMessage(MSG_FIRST_DEVICE);
                        } else {
                            handler.sendEmptyMessage(MSG_ADD_DEVICE);
                        }
                    } else {
                        Toast.makeText(mActivity, "设备不符合合规策略：" + complianced + ",请检查后再添加", Toast.LENGTH_LONG).show();
                    }
                }
            });
            if (pwdET.getVisibility() != View.VISIBLE) {
                t.start();
            } else {
                userPwd = pwdET.getText().toString();
            }
        }
    };

    //认证并跳转到launcher页面
    View.OnClickListener authAndJumpListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Auth
            userAccount = userAccountET.getText().toString().trim();
            userPwd = pwdET.getText().toString();
            if (userAccount.length() < 1 || userPwd.length() < 1) {
                Toast.makeText(mActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    String deviceID = DeviceUtils.getInstance(mActivity).getsDeviceIdSHA1();
                    boolean authRst = AuthUtils.login(userAccount, userPwd, deviceID);
                    if (authRst == CommonUtils.SUCCESS) {
                        String userID = AuthUtils.getUserID(userAccount);
                        Intent i = new Intent(mActivity, HomeScreen.class);
                        i.putExtra("UserID", userID);
                        startActivity(i);
                    }

                }
            });
            t.start();
        }
    };

    //认证并新增设备
    View.OnClickListener authAndAddDevice = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Auth
            userAccount = userAccountET.getText().toString().trim();
            userPwd = pwdET.getText().toString();
            if (userAccount.length() < 1 || userPwd.length() < 1) {
                Toast.makeText(mActivity, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    String deviceID = DeviceUtils.getInstance(mActivity).getsDeviceIdSHA1();
                    boolean authRst = AuthUtils.login(userAccount, userPwd, deviceID);
                    if (authRst == CommonUtils.SUCCESS) {
                        //dialog： if add the device
                        addNewDeviceDialog();
                    } else {
                        Toast.makeText(mActivity, "用户名和密码不匹配", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            t.start();
        }
    };

    //keyboard
    View.OnTouchListener showKeyboard = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int inputType = ((EditText) v).getInputType();
            ((EditText) v).setInputType(InputType.TYPE_NULL);
            if (v == userAccountET) {
                if (keyboard1 == null) {
                    keyboard1 = new KeyboardUtil(mActivity, mActivity, (EditText) v);
                }
                keyboard1.showKeyboard();
            } else {
                if (keyboard2 == null) {
                    keyboard2 = new KeyboardUtil(mActivity, mActivity, (EditText) v);
                }
                keyboard2.showKeyboard();
            }
            ((EditText) v).setInputType(inputType);
            return false;
        }
    };

    private void initView() {
        userAccountET = (EditText) findViewById(R.id.userAccount);
        nextBt = (Button) findViewById(R.id.next);
        pwdET = (EditText) findViewById(R.id.pwd);
        userAccountET.setOnTouchListener(showKeyboard);
        textView2 = (TextView) findViewById(R.id.textView2);
        nextBt.setOnClickListener(checkUserDeviceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
