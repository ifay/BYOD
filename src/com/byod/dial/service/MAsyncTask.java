package com.byod.dial.service;
//a-z搜索

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.byod.bean.ContactBean;
import com.byod.utils.ToPinYin;

import java.util.ArrayList;

public class MAsyncTask extends AsyncTask<Cursor, Void, ArrayList<ContactBean>> {

    private static final String TAG = MAsyncTask.class.getSimpleName();

    /**
     * 开始整理
     */
    public static final int DOWNLOADING_START_MESSAGE = 7;
    /**
     * 整理结束
     */
    public static final int DOWNLOAD_END_MESSAGE = 17;


    private Context mContext = null;
    private Handler mHandler = null;

    protected MAsyncTask(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        sendStartMessage(DOWNLOADING_START_MESSAGE);
    }

    @Override
    protected ArrayList<ContactBean> doInBackground(Cursor... params) {
        Cursor cursor = params[0];

        ArrayList<ContactBean> ciList = new ArrayList<ContactBean>();
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    int contactId = cursor.getInt(4);
                    ContactBean contactInfo = new ContactBean();
                    contactInfo.setContactId(contactId);
                    contactInfo.setPhoneNum(number);
                    contactInfo.setDisplayName(name);
                    if (contactInfo.getDisplayName() == null) {
                        contactInfo.setDisplayName(contactInfo.getPhoneNum());
                    }
                    contactInfo.setFormattedNumber(getNameNum(contactInfo.getDisplayName() + ""));
                    contactInfo.setPinyin(ToPinYin.getPinYin(contactInfo.getDisplayName() + ""));
                    ciList.add(contactInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ciList;
    }

    @Override
    protected void onPostExecute(ArrayList<ContactBean> result) {
        sendEndMessage(DOWNLOAD_END_MESSAGE, result);
    }

    public static void startRequestServerData(Context context, Handler handler, Cursor cursor) {
        new MAsyncTask(context, handler).execute(cursor);
    }

    /**
     * 开始整理
     *
     * @param messageWhat
     */
    private void sendStartMessage(int messageWhat) {
        Message message = new Message();
        message.what = messageWhat;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    /**
     * 整理结束
     *
     * @param messageWhat
     */
    private void sendEndMessage(int messageWhat, ArrayList<ContactBean> result) {
        Message message = new Message();
        message.what = messageWhat;
        Bundle bundle = new Bundle();
        bundle.putSerializable("完成", result);
        message.setData(bundle);
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    private String getNameNum(String name) {
        try {
            if (name != null && name.length() != 0) {
                int len = name.length();
                char[] nums = new char[len];
                for (int i = 0; i < len; i++) {
                    String tmp = name.substring(i);
                    nums[i] = getOneNumFromAlpha(ToPinYin.getPinYin(tmp).toLowerCase().charAt(0));
                }
                return new String(nums);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private char getOneNumFromAlpha(char firstAlpha) {
        switch (firstAlpha) {
            case 'a':
            case 'b':
            case 'c':
                return '2';
            case 'd':
            case 'e':
            case 'f':
                return '3';
            case 'g':
            case 'h':
            case 'i':
                return '4';
            case 'j':
            case 'k':
            case 'l':
                return '5';
            case 'm':
            case 'n':
            case 'o':
                return '6';
            case 'p':
            case 'q':
            case 'r':
            case 's':
                return '7';
            case 't':
            case 'u':
            case 'v':
                return '8';
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return '9';
            default:
                return '0';
        }
    }
}
