package com.byod.sms.activities;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.byod.R;
import com.byod.bean.SMSBean;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.sms.adapter.SMSAdapter;
import com.byod.sms.data.SMSAsyncQueryFactory;
import com.byod.utils.BaseIntentUtil;
import com.byod.utils.RexseeSMS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSActivity extends Activity implements IAsyncQueryHandler {
    private ListView listView;
    private SMSAdapter adapter;
    private RexseeSMS rsms;
    private Button newSms;
    private IAsyncQueryFactory mAsyncQueryFactory;
    private List<SMSBean> mSmsList;
    private HashMap<String, SMSBean> mSMSIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_page);
        listView = (ListView) findViewById(R.id.list);
        adapter = new SMSAdapter(SMSActivity.this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, String> map = new HashMap<String, String>();
                SMSBean sb = adapter.getItem(position);
                map.put("phoneNumber", sb.getAddress());
                map.put("threadId", sb.getThread_id());
                BaseIntentUtil.intentSysDefault(SMSActivity.this, MessageBoxList.class, map);
            }
        });

        newSms = (Button) findViewById(R.id.newSms);
        newSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseIntentUtil.intentSysDefault(SMSActivity.this, NewSMSActivity.class, null);
            }
        });

//        mSmsList = new ArrayList<SMSBean>();
//        SMSBean sb = new SMSBean();
//        sb.setThread_id("1");
//        sb.setAddress("小张");
//        sb.setDate(System.currentTimeMillis());
//        mSmsList.add(sb);
//        adapter.assignment(mSmsList);


        mAsyncQueryFactory = new SMSAsyncQueryFactory(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
        query.startQuery();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mSMSIdMap = new HashMap<String, SMSBean>();
            mSmsList = new ArrayList<SMSBean>();

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                SMSBean mmt = new SMSBean();
                int smsId = cursor.getInt(0);
                String number = cursor.getString(2);
                String body = cursor.getString(3);
                long date = cursor.getLong(4);
                int threadId = cursor.getInt(5);
                int read = cursor.getInt(6);

                SMSBean sb = new SMSBean();
                sb.setAddress(number);
                sb.setDate(date);
                sb.setMsg_snippet(body);
                sb.setThread_id(String.valueOf(threadId));
                sb.setRead(String.valueOf(read));

                mSMSIdMap.put(number, sb);
            }
            cursor.close();
            mSmsList = new ArrayList<SMSBean>(mSMSIdMap.values());
            if (mSmsList.size() > 0) {
                adapter.assignment(mSmsList);
            }
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }
}
