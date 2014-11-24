package com.byod.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

public class AnimationTabHost extends TabHost {
    private boolean isOpenAnimation;
    private int mTabCount;

    public AnimationTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        isOpenAnimation = false;
    }

    public void setOpenAnimation(boolean isOpenAnimation) {
        this.isOpenAnimation = isOpenAnimation;
    }

    public boolean setTabAnimation(int[] animationResIDs) {
        return false;
    }

    public int getTabCount() {
        return mTabCount;
    }

    @Override
    public void addTab(TabSpec tabSpec) {
        mTabCount++;
        super.addTab(tabSpec);
    }

    @Override
    public void setCurrentTab(int index) {

        if (null != getCurrentView()) {
            if (isOpenAnimation) {
            }
        }
        super.setCurrentTab(index);
        if (isOpenAnimation) {
        }
    }
}
