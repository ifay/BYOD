package com.byod.contacts.view.other;

import android.app.Activity;
import android.util.DisplayMetrics;

public class SystemScreenInfo {

    public static int SYS_SCREEN_WIDTH;
    public static int SYS_SCREEN_HEIGHT;
    public static int CONTACT_GROUP_LABLE;

    public static void getSystemInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        SYS_SCREEN_WIDTH = dm.widthPixels;
        SYS_SCREEN_HEIGHT = dm.heightPixels;

        CONTACT_GROUP_LABLE = SYS_SCREEN_WIDTH / 5 * 3;
    }
}
