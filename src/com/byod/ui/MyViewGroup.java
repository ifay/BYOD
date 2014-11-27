package com.byod.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class MyViewGroup extends ViewGroup {

    private final static int VIEW_MARGIN = 2;
    private int maxWidth = 0;
    private int maxHeight = 60;

    public MyViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        final int count = getChildCount();
        int row = 0;// which row lay you view relative to parent
        int lengthX = arg1;    // right position of child relative to parent
        int lengthY = arg2;    // bottom position of child relative to parent
        for (int i = 0; i < count; i++) {

            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            //            int height = child.getMeasuredHeight();
            int height = maxHeight; //限制子节点的高度
            lengthX += width + VIEW_MARGIN;

            lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + arg2;
            if (width + VIEW_MARGIN > maxWidth) {
                maxWidth = width + VIEW_MARGIN;
            }

            if (lengthX > arg3) {
                lengthX = width + VIEW_MARGIN + arg1;
                row++;
                lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + arg2;

            }
            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
        }
    }
}
