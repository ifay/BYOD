/**
 * 
 */
package com.byod.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;

/**
 * @author ifay
 *
 */
public class BYODEditText extends EditText implements OnLongClickListener{

    /**
     * @param context
     */
    public BYODEditText(Context context) {
        super(context);
    }

    public BYODEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public BYODEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onLongClick(View v) {
        ///TODO 长按弹出对话框，提示选择粘贴或复制
        return false;
    }

    
}
