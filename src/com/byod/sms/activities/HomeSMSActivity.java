package com.byod.sms.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.byod.R;
import com.byod.contacts.bean.SMSBean;
import com.byod.contacts.uitl.BaseIntentUtil;
import com.byod.contacts.uitl.RexseeSMS;
import com.byod.contacts.view.adapter.HomeSMSAdapter;
import com.byod.contacts.view.sms.MessageBoxList;
import com.byod.contacts.view.sms.NewSMSActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeSMSActivity extends Activity {

	private ListView listView;
	private HomeSMSAdapter adapter;
	private RexseeSMS rsms;
	private Button newSms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		init();
		
	}

	public void init(){

		setContentView(R.layout.home_sms_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		listView = (ListView) findViewById(R.id.list);
		adapter = new HomeSMSAdapter(HomeSMSActivity.this);
		
		rsms = new RexseeSMS(HomeSMSActivity.this);
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
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
			}
		});
		
		newSms = (Button) findViewById(R.id.newSms);
		newSms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, NewSMSActivity.class, null);
			}
		});
		
		
	}






}
