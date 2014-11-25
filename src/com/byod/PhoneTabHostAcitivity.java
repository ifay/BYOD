package com.byod;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.byod.contacts.activities.PeopleActivity;
import com.byod.contacts.service.T9Service;
import com.byod.contacts.view.other.SystemScreenInfo;
import com.byod.dial.activities.DialActivity;
import com.byod.sms.activities.SMSActivity;
import com.byod.ui.AnimationTabHost;

public class PhoneTabHostAcitivity extends TabActivity {
    private static final String TAG = "PhoneTabHostAcitivity";
    public static final String EXTRA_PAGE = "page";
    private AnimationTabHost mTabHost;
    private TabWidget mTabWidget;
    private ImageView cursor;
    private int offset = 0;
    private int currIndex = 0;
    private int bmpW;
    private ImageView[] views;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_hometabhost);
        Intent startService = new Intent(this, T9Service.class);
        startService(startService);
        SystemScreenInfo.getSystemInfo(PhoneTabHostAcitivity.this);

        InitImageView();
        mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
        mTabHost = (AnimationTabHost) findViewById(android.R.id.tabhost);
        Log.d(TAG, "Go to page: " + getIntent().getIntExtra(EXTRA_PAGE, 0));
        init(getIntent().getIntExtra(EXTRA_PAGE, 0));
    }

    private int getImageId(int index, boolean isSelect) {
        int result = -1;
        switch (index) {
            case 0:
                result = isSelect ? R.drawable.tab_dial_selected : R.drawable.tab_dial_normal;
                break;
            case 1:
                result = isSelect ? R.drawable.tab_contact_selected : R.drawable.tab_contact_normal;
                break;
            case 2:
                result = isSelect ? R.drawable.tab_sms_selected : R.drawable.tab_sms_normal;
                break;
        }
        return result;
    }

    private void initBottomMenu(int page) {
        int viewCount = mTabWidget.getChildCount();
        views = new ImageView[viewCount];
        for (int i = 0; i < views.length; i++) {
            View v = (LinearLayout) mTabWidget.getChildAt(i);
            views[i] = (ImageView) v.findViewById(R.id.main_activity_tab_image);
        }
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                int tabID = Integer.valueOf(tabId);
                views[currIndex].setImageResource(getImageId(currIndex, false));
                views[tabID].setImageResource(getImageId(tabID, true));
                onPageSelected(tabID);
            }
        });
        mTabHost.setCurrentTab(page);
    }

    private void init(final int page) {
        setIndicator(getString(R.string.dial), 0, new Intent(this, DialActivity.class), R.drawable.tab_dial_selected);
        setIndicator(getString(R.string.contacts), 1, new Intent(this, PeopleActivity.class), R.drawable.tab_contact_normal);
        setIndicator(getString(R.string.sms), 2, new Intent(this, SMSActivity.class), R.drawable.tab_sms_normal);
        mTabHost.setOpenAnimation(true);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                initBottomMenu(page);
            }
        }, 300);
    }

    private void setIndicator(String ss, int tabId, Intent intent, int image_id) {
        View localView = LayoutInflater.from(this.mTabHost.getContext()).inflate(R.layout.tab_widget_view, null);
        ((ImageView) localView.findViewById(R.id.main_activity_tab_image)).setImageResource(image_id);
        ((TextView) localView.findViewById(R.id.main_activity_tab_text)).setText(ss);
        String str = String.valueOf(tabId);
        TabHost.TabSpec localTabSpec = mTabHost.newTabSpec(str).setIndicator(localView).setContent(intent);
        mTabHost.addTab(localTabSpec);
    }

    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.main_tab_anim_light).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 4 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);
    }

    public void onPageSelected(int arg0) {
        int one = offset * 2 + bmpW;
        Animation animation = null;
        animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);
        currIndex = arg0;
        animation.setFillAfter(true);
        animation.setDuration(300);
        cursor.startAnimation(animation);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        }
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}