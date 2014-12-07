package com.byod.sms.activities;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.byod.R;
import com.byod.bean.ContactBean;
import com.byod.contacts.data.ContactsAsyncQueryFactory;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.sms.adapter.SelectContactsToSendAdapter;
import com.byod.ui.QuickAlphabeticBar;
import com.byod.utils.BaseIntentUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectContactsToSendActivity extends Activity implements IAsyncQueryHandler {

    private SelectContactsToSendAdapter adapter;
    private ListView personList;

    private List<ContactBean> list;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;

    private List<ContactBean> selectContactList = new ArrayList<ContactBean>();
    private Button returnBtn;
    private Button doneBtn;
    private Map<String, String> selectMap = null;

    private IAsyncQueryFactory mAsyncQueryFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contacts_to_send);


        if (null != getIntent().getStringExtra("data")) {
            String data = getIntent().getStringExtra("data");
            Gson gson = new Gson();
            Type listRet = new TypeToken<List<ContactBean>>() {
            }.getType();
            selectContactList = gson.fromJson(data, listRet);
            selectMap = new HashMap<String, String>();
            for (ContactBean cb : selectContactList) {
                selectMap.put(cb.getPhoneNum(), cb.getDisplayName());
            }
        }

        personList = (ListView) findViewById(R.id.list);

        alpha = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

        returnBtn = (Button) findViewById(R.id.btn_return);
        doneBtn = (Button) findViewById(R.id.btn_done);

        returnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectContactsToSendActivity.this.finish();
            }
        });
        doneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String data = gson.toJson(selectContactList);
                Map<String, String> map = new HashMap<String, String>();
                map.put("list", data);
                BaseIntentUtil.intentSysDefault(SelectContactsToSendActivity.this, NewSMSActivity.class, map);
            }
        });

        mAsyncQueryFactory = new ContactsAsyncQueryFactory(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
        query.startQuery();
    }

    /**
     * 查询结束的回调函数
     */
    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            list = new ArrayList<ContactBean>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int contactId = cursor.getInt(0);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String sortKey = cursor.getString(3);
                String lookUpKey = cursor.getString(4);

                ContactBean cb = new ContactBean();
                cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//						cb.setPhoneNum(number.substring(3));
//					} else {
                cb.setPhoneNum(number);
//					}
                cb.setSortKey(sortKey);
                cb.setContactId(contactId);
                cb.setLookUpKey(lookUpKey);

                if (null == selectMap) {
                } else {
                    if (selectMap.containsKey(number)) {
                        cb.setSelected(1);
                    }
                }

                list.add(cb);
            }
            cursor.close();
            if (list.size() > 0) {
                setAdapter(list);
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


    private void setAdapter(List<ContactBean> list) {
        adapter = new SelectContactsToSendAdapter(this, list, alpha);
        personList.setAdapter(adapter);
        alpha.init(SelectContactsToSendActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                boolean check = SelectContactsToSendAdapter.isSelected.get(position);
                if (check) {
                    SelectContactsToSendAdapter.isSelected.put(position, false);
                    selectContactList.remove(cb);
                } else {
                    SelectContactsToSendAdapter.isSelected.put(position, true);
                    selectContactList.add(cb);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


}
