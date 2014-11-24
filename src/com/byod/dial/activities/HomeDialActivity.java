package com.byod.dial.activities;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.byod.R;
import com.byod.contacts.application.MyApplication;
import com.byod.contacts.bean.CallLogBean;
import com.byod.contacts.view.adapter.HomeDialAdapter;
import com.byod.contacts.view.adapter.T9Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDialActivity extends Activity implements OnClickListener {
	
	private AsyncQueryHandler asyncQuery;
	
	private HomeDialAdapter adapter;
	private ListView callLogList;
	
	private List<CallLogBean> list;
	
	private LinearLayout bohaopan;
	private LinearLayout keyboard_show_ll;
	private Button keyboard_show;
	
	private Button phone_view;
	private Button delete;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private SoundPool spool;
	private AudioManager am = null;
	
	private MyApplication application;
	private ListView listView;
	private T9Adapter t9Adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_dial_page);
		
		application = (MyApplication)getApplication();
		listView = (ListView) findViewById(R.id.contact_list);
		
		bohaopan = (LinearLayout) findViewById(R.id.bohaopan);
		keyboard_show_ll = (LinearLayout) findViewById(R.id.keyboard_show_ll);
		keyboard_show = (Button) findViewById(R.id.keyboard_show);
		callLogList = (ListView)findViewById(R.id.call_log_list);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		
		keyboard_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialPadShow();
			}
		});
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 5);
//		map.put(0, spool.load(this, R.raw.dtmf0, 0));
//		map.put(1, spool.load(this, R.raw.dtmf1, 0));
//		map.put(2, spool.load(this, R.raw.dtmf2, 0));
//		map.put(3, spool.load(this, R.raw.dtmf3, 0));
//		map.put(4, spool.load(this, R.raw.dtmf4, 0));
//		map.put(5, spool.load(this, R.raw.dtmf5, 0));
//		map.put(6, spool.load(this, R.raw.dtmf6, 0));
//		map.put(7, spool.load(this, R.raw.dtmf7, 0));
//		map.put(8, spool.load(this, R.raw.dtmf8, 0));
//		map.put(9, spool.load(this, R.raw.dtmf9, 0));
//		map.put(11, spool.load(this, R.raw.dtmf11, 0));
//		map.put(12, spool.load(this, R.raw.dtmf12, 0));
		
		phone_view = (Button) findViewById(R.id.phone_view);
		phone_view.setOnClickListener(this);
		phone_view.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(null == application.getContactBeanList() || application.getContactBeanList().size()<1 || "".equals(s.toString())){
					listView.setVisibility(View.INVISIBLE);
					callLogList.setVisibility(View.VISIBLE);
				}else{
					if(null == t9Adapter){
						t9Adapter = new T9Adapter(HomeDialActivity.this);
						t9Adapter.assignment(application.getContactBeanList());
//						TextView tv = new TextView(HomeDialActivity.this);
//						tv.setBackgroundResource(R.drawable.dial_input_bg2);
//						listView.addFooterView(tv);
						listView.setAdapter(t9Adapter);
						listView.setTextFilterEnabled(true);
						listView.setOnScrollListener(new OnScrollListener() {
							@Override
							public void onScrollStateChanged(AbsListView view, int scrollState) {
								if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
									if(bohaopan.getVisibility() == View.VISIBLE){
										bohaopan.setVisibility(View.GONE);
										keyboard_show_ll.setVisibility(View.VISIBLE);
									}
								}
							}
							@Override
							public void onScroll(AbsListView view, int firstVisibleItem,
									int visibleItemCount, int totalItemCount) {
							}
						});
					}else{
						callLogList.setVisibility(View.INVISIBLE);
						listView.setVisibility(View.VISIBLE);
						t9Adapter.getFilter().filter(s);
					}
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		delete.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				phone_view.setText("");
				return false;
			}
		});
		
		for (int i = 0; i < 12; i++) {
			View v = findViewById(R.id.dialNum1 + i);
			v.setOnClickListener(this);
		}
		
		init();
		
	}
	
	private void init(){
		Uri uri = CallLog.Calls.CONTENT_URI;
		
		String[] projection = { 
				CallLog.Calls.DATE,
				CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE,
				CallLog.Calls.CACHED_NAME,
				BaseColumns._ID
		}; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);  
	}
	

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
//					String date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
					String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
					int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));//缓存的名称与电话号码，如果它的存在
					int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));

					CallLogBean clb = new CallLogBean();
					clb.setId(id);
					clb.setNumber(number);
					clb.setName(cachedName);
					if(null == cachedName || "".equals(cachedName)){
						clb.setName(number);
					}
					clb.setType(type);
					clb.setDate(sfd.format(date));
					
					list.add(clb);
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}


	private void setAdapter(List<CallLogBean> list) {
		adapter = new HomeDialAdapter(this, list);
//		TextView tv = new TextView(this);
//		tv.setBackgroundResource(R.drawable.dial_input_bg2);
//		callLogList.addFooterView(tv);
		callLogList.setAdapter(adapter);
		callLogList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					if(bohaopan.getVisibility() == View.VISIBLE){
						bohaopan.setVisibility(View.GONE);
						keyboard_show_ll.setVisibility(View.VISIBLE);
					}
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		callLogList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				
			}
		});
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialNum0:
			if (phone_view.getText().length() < 12) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum1:
			if (phone_view.getText().length() < 12) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum2:
			if (phone_view.getText().length() < 12) {
				play(2);
				
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum3:
			if (phone_view.getText().length() < 12) {
				play(3);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum4:
			if (phone_view.getText().length() < 12) {
				play(4);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum5:
			if (phone_view.getText().length() < 12) {
				play(5);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum6:
			if (phone_view.getText().length() < 12) {
				play(6);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum7:
			if (phone_view.getText().length() < 12) {
				play(7);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum8:
			if (phone_view.getText().length() < 12) {
				play(8);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialNum9:
			if (phone_view.getText().length() < 12) {
				play(9);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialx:
			if (phone_view.getText().length() < 12) {
				play(11);
				input(v.getTag().toString());
			}
			break;
		case R.id.dialj:
			if (phone_view.getText().length() < 12) {
				play(12);
				input(v.getTag().toString());
			}
			break;
		case R.id.delete:
			delete();
			break;
		case R.id.phone_view:
			if (phone_view.getText().toString().length() >= 4) {
				call(phone_view.getText().toString());
			}
			break;
		default:
			break;
		}
	}
	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}
	private void input(String str) {
		String p = phone_view.getText().toString();
		phone_view.setText(p + str);
	}
	private void delete() {
		String p = phone_view.getText().toString();
		if(p.length()>0){
			phone_view.setText(p.substring(0, p.length()-1));
		}
	}
	private void call(String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
	}
	
	
	
	
	
	

	public void dialPadShow(){
		if(bohaopan.getVisibility() == View.VISIBLE){
			bohaopan.setVisibility(View.GONE);
			keyboard_show_ll.setVisibility(View.VISIBLE);
		}else{
			bohaopan.setVisibility(View.VISIBLE);
			keyboard_show_ll.setVisibility(View.INVISIBLE);
		}
	}
	
	
	
}
