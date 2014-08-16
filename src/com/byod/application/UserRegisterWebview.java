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
 */
public class UserRegisterWebview extends Activity {

    private static final String LOG_TAG = "UserRegisterWebview";
    //url 最好写在文件中，至少前缀可以写在文件中。后面的子页面做成key-value对，由公司配置。
    //最后写成 url = IP+RegisterPage;
    private String url = "http://192.168.0.117:8080/UIA/adduser.jsp";

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
