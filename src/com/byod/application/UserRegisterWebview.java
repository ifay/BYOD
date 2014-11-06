/**
 * 
 */
package com.byod.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.byod.R;

/**
 * @author ifay
 * 用户注册页面，尝试web View方式实现
 * 步骤一（服务器端）：管理员在UIA上录入用户的基本信息，用户获得默认权限-最低的权限
 * 步骤二：用户首次通过初始密码登入系统
 *      2.1同步默认安全策略到本地，并进行检查
 *      2.2完善用户详细信息
 *      2.3完善挑战问题答案库
 * 步骤三：设备绑定
 * 
 * TODO 考虑把登陆INFO的方法做成服务？？还是直接用WebView展示？
 */
public class UserRegisterWebview extends Activity {

    private static final String LOG_TAG = "UserRegisterWebview";
    //url ip通过由界面输入。后面的子页面做成key-value对，存于文件中，由公司配置，可推送更新。
    //最后写成 url = IP+RegisterPage;
    private String url = "http://192.168.1.103:8080/INFO/";

    private WebView registerView = null;
    private WebSettings webSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        registerView = (WebView)findViewById(R.id.userRegisterView);
        //set view properties
        webSettings = registerView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        //set show view in this app
        registerView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading..."+progress+"%");
                setProgress(progress*100);
                if (progress == 100) {
                    setTitle(R.string.app_name);
                }
            }
        });
        registerView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                Toast.makeText(UserRegisterWebview.this, "Oh no:" + description, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        registerView.loadUrl(url);
        
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}
