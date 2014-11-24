/**
 *
 */
package com.byod.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.byod.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ifay
 *         定义键盘的按键事件，随机键盘
 */
public class KeyboardUtil {

    private Context ctx;
    private Activity act;
    private KeyboardView keyboardView;
    private Keyboard kNum;  //数字键盘
    private Keyboard kChar; //字母键盘
    private boolean isNum = false;  //是否数字键盘
    private boolean isUpper = false;    //是否大写

    private EditText ed;

    public KeyboardUtil(Activity act, Context ctx, EditText edit) {
        this.act = act;
        this.ctx = ctx;
        this.ed = edit;
        kNum = new Keyboard(ctx, R.xml.symbols);
        kChar = new Keyboard(ctx, R.xml.qwerty);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(kChar);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    public void showKeyboard() {
        if (keyboardView.getVisibility() != View.VISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            switch (primaryCode) {
                case Keyboard.KEYCODE_CANCEL: // 完成
                    hideKeyboard();
                    break;
                case Keyboard.KEYCODE_DELETE: // delete
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT: //大小写切换
                    switchKey();
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE:  //键盘切换
                    changeKeyboard();
                    break;
                case 57419: // go left
                    if (start > 0) {
                        ed.setSelection(start - 1);
                    }
                    break;
                case 57421: // go right
                    if (start < ed.length()) {
                        ed.setSelection(start + 1);
                    }
                    break;
                default:
                    editable.insert(start, String.valueOf((char) primaryCode));
                    randomKey();
                    break;
            }
        }

        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }
    };

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        if (keyboardView.getVisibility() == View.VISIBLE) {
            keyboardView.setVisibility(View.GONE);
        }
    }

    public boolean keyboardIsShown() {
        return keyboardView.getVisibility() == View.VISIBLE ? true : false;
    }

    /**
     * 切换数字、字母键盘
     */
    public void changeKeyboard() {
        if (isNum) {
            keyboardView.setKeyboard(kChar);
        } else {
            keyboardView.setKeyboard(kNum);
        }
        isNum = !isNum;
        randomKey();
    }

    /**
     * 随机布局
     */
    private void randomKey() {
        List<Key> keyList;
        List<Key> newkeyList = new ArrayList<Key>();
        if (isNum) {
            //数字键
            keyList = kNum.getKeys();
            for (int i = 0; i < keyList.size(); i++) {
                if (keyList.get(i).label != null && isNumberKey(keyList.get(i))) {
                    newkeyList.add(keyList.get(i));
                }
            }
        } else {
            //字母键
            keyList = kChar.getKeys();
            for (int i = 0; i < keyList.size(); i++) {
                if (keyList.get(i).label != null && isWordKey(keyList.get(i))) {
                    newkeyList.add(keyList.get(i));
                }
            }
        }//if else
        int size = newkeyList.size();
        for (int i = 0; i < size; i++) {
            int random_a = (int) (Math.random() * (size));
            int random_b = (int) (Math.random() * (size));

            int code = newkeyList.get(random_a).codes[0];
            CharSequence label = newkeyList.get(random_a).label;

            newkeyList.get(random_a).codes[0] = newkeyList.get(random_b).codes[0];
            newkeyList.get(random_a).label = newkeyList.get(random_b).label;

            newkeyList.get(random_b).codes[0] = code;
            newkeyList.get(random_b).label = label;
        }
        if (isNum) {
            keyboardView.setKeyboard(kNum);
        } else {
            keyboardView.setKeyboard(kChar);
        }
    }

    private boolean isWordKey(Key k) {
        if (isUpper) {
            return (k.codes[0] > 64 && k.codes[0] < 91);
        } else
            return (k.codes[0] > 96 && k.codes[0] < 123);
    }

    /**
     * 数字code:48-57
     *
     * @param k
     * @return
     */
    private boolean isNumberKey(Key k) {
        return (k.codes[0] > 47 && k.codes[0] < 58);
    }

    /**
     * 大小写切换 ：小-32=大
     * 大写字母：65-90
     * 小写字母：97-122
     */
    public void switchKey() {
        List<Key> keylist = kChar.getKeys();
        if (isUpper) {
            // 大写转小写
            for (Key key : keylist) {
                if (key.label != null && key.codes[0] < 91 && key.codes[0] > 64) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] += 32;
                }
            }
        } else {
            for (Key key : keylist) {
                if (key.label != null && key.codes[0] < 123 && key.codes[0] > 96) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] -= 32;
                }
            }
        }
        isUpper = !isUpper;
        keyboardView.setKeyboard(kChar);
    }
}
