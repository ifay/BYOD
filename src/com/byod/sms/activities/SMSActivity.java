package com.byod.sms.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.byod.R;
import com.byod.bean.SMSBean;
import com.byod.utils.BaseIntentUtil;
import com.byod.utils.RexseeSMS;
import com.byod.sms.adapter.SMSAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSActivity extends Activity {
    private ListView listView;
    private SMSAdapter adapter;
    private RexseeSMS rsms;
    private Button newSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_page);
        init();
    }

    public void init() {
        listView = (ListView) findViewById(R.id.list);
        adapter = new SMSAdapter(SMSActivity.this);
        rsms = new RexseeSMS(SMSActivity.this);
        List<SMSBean> list_mmt = rsms.getThreadsNum(rsms.getThreads(0));
        adapter.assignment(list_mmt);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
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
        newSms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseIntentUtil.intentSysDefault(SMSActivity.this, NewSMSActivity.class, null);
            }
        });
    }
}
