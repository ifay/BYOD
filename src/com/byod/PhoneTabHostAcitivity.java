package com.byod;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import com.byod.contacts.activities.PeopleActivity;
import com.byod.dial.service.T9Service;
import com.byod.dial.activities.DialActivity;
import com.byod.sms.activities.SMSActivity;

public class PhoneTabHostAcitivity extends TabActivity {
    private static final String TAG = "PhoneTabHostAcitivity";
    public static final String EXTRA_PAGE = "page";
    private TabHost mTabHost;
    private TabWidget mTabWidget;
    private int currIndex = 0;
    private ImageView[] views;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_hometabhost);
        Intent startService = new Intent(this, T9Service.class);
        startService(startService);

        mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
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
            views[i] = (ImageView) mTabWidget.getChildAt(i).findViewById(R.id.main_activity_tab_image);
        }
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                int tabID = Integer.valueOf(tabId);
                views[currIndex].setImageResource(getImageId(currIndex, false));
                views[tabID].setImageResource(getImageId(tabID, true));
                currIndex = tabID;
            }
        });
        mTabHost.setCurrentTab(page);
        views[page].setImageResource(getImageId(page, true));
    }

    private void init(final int page) {
        setIndicator(getString(R.string.dial), 0, new Intent(this, DialActivity.class), R.drawable.tab_dial_normal);
        setIndicator(getString(R.string.contacts), 1, new Intent(this, PeopleActivity.class), R.drawable.tab_contact_normal);
        setIndicator(getString(R.string.sms), 2, new Intent(this, SMSActivity.class), R.drawable.tab_sms_normal);
        initBottomMenu(page);
    }

    private void setIndicator(String ss, int tabId, Intent intent, int image_id) {
        View localView = LayoutInflater.from(this).inflate(R.layout.tab_widget_view, null);
        ((ImageView) localView.findViewById(R.id.main_activity_tab_image)).setImageResource(image_id);
        ((TextView) localView.findViewById(R.id.main_activity_tab_text)).setText(ss);
        String str = String.valueOf(tabId);
        TabHost.TabSpec localTabSpec = mTabHost.newTabSpec(str).setIndicator(localView).setContent(intent);
        mTabHost.addTab(localTabSpec);
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