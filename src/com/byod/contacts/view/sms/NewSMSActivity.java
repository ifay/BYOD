package com.byod.contacts.view.sms;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.R;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.uitl.BaseIntentUtil;
import com.byod.contacts.view.adapter.NewSmsAdapter;
import com.byod.contacts.view.ui.MyViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSMSActivity extends Activity {

    private Button btn_return;
    private Button add_btn;
    private Button fasong;
    private List<ContactBean> selectContactList = null;

    private MyViewGroup mvg;
    private LinearLayout ll;
    private EditText etMess;
    private int extiTextId = 100001;
    private String[] chars = new String[]{" ", ","};

    private ListView queryListView;
    private NewSmsAdapter adapter;

    private AsyncQueryHandler asyncQuery;
    private List<ContactBean> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sms);


        queryListView = (ListView) findViewById(R.id.list);

        btn_return = (Button) findViewById(R.id.btn_return);
        btn_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NewSMSActivity.this.finish();
            }
        });
        add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == etMess || "".equals(etMess.getText().toString())) {
                } else {
                    String phoneNum = etMess.getText().toString();
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(phoneNum, phoneNum);
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                    }
                }

                if (null == selectContactList || selectContactList.size() < 1) {
                    BaseIntentUtil.intentSysDefault(NewSMSActivity.this, SelectContactsToSendActivity.class, null);
                } else {
                    Gson gson = new Gson();
                    String data = gson.toJson(selectContactList);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("data", data);
                    BaseIntentUtil.intentSysDefault(NewSMSActivity.this, SelectContactsToSendActivity.class, map);
                }
            }
        });

        fasong = (Button) findViewById(R.id.fasong);
        fasong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == etMess || "".equals(etMess.getText().toString())) {
                } else {
                    String phoneNum = etMess.getText().toString();
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(phoneNum, phoneNum);
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                    }
                }


                if (null == selectContactList || selectContactList.size() < 1) {
                    Toast.makeText(NewSMSActivity.this, "请输入发送目标", Toast.LENGTH_SHORT).show();
                } else {

                    for (ContactBean cb : selectContactList) {

                        System.out.println(cb.getDisplayName());
                        System.out.println(cb.getPhoneNum());
                        System.out.println("------");
                    }
                }

            }
        });


        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        query();


        initMyGroupView();


        if (null != getIntent().getStringExtra("list")) {
            String data = getIntent().getStringExtra("list");
            Gson gson = new Gson();
            Type listRet = new TypeToken<List<ContactBean>>() {
            }.getType();
            selectContactList = gson.fromJson(data, listRet);
            for (ContactBean cb : selectContactList) {
                createView2(cb.getDisplayName().trim());
                final View child = mvg.getChildAt(mvg.getChildCount() - 1);
                autoHeight(child);
            }
        }


    }


    private void query()

    {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = {
                BaseColumns._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
        }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
    }


    private void initMyGroupView() {
        ll = (LinearLayout) findViewById(R.id.l1);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        /**********************************************************************************************/
        mvg = new MyViewGroup(NewSMSActivity.this);
        mvg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 70));
        //		mvg.setBackgroundColor(Color.GREEN);
        etMess = new EditText(NewSMSActivity.this);
        etMess.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        etMess.setSelection(etMess.getText().length());
        etMess.setGravity(Gravity.CENTER_VERTICAL);
        etMess.setMinWidth(100);
        etMess.setHeight(60);
        etMess.setTag("edit");
        etMess.getBackground().setAlpha(0);
        etMess.setId(extiTextId);
        etMess.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isNum(s.toString())) {
                    if (s.length() >= 1) {
                        boolean bool = false;
                        //length() == 15直接生成按钮
                        if (s.length() == 15) {
                            bool = true;
                        }
                        //字数没有满足15个验证是否有空格
                        if (!bool) {
                            String c = s.toString().substring(start, start + count);
                            for (int i = 0; i < chars.length; i++) {
                                if (chars[i].equals(c)) {
                                    bool = true;
                                    break;
                                }
                            }
                        }
                        //bool == true 生成Button
                        if (bool) {
                            createView1(s.toString(), s.toString());
                            etMess.setText("");
                        }
                        //检测输入框数据是否已经换行
                        final View child = mvg.getChildAt(mvg.getChildCount() - 1);
                        autoHeight(child);
                    }
                } else {
                    adapter.getFilter().filter(s);
                    queryListView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mvg.addView(etMess);
        ll.addView(mvg);
        etMess.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isNum(etMess.getText().toString().trim())) {
                        createView1(etMess.getText().toString().trim(), etMess.getText().toString().trim());
                        etMess.setText("");
                    } else {
                        etMess.setText("");
                        queryListView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 为MyViewGroup自动计算高度
     *
     * @param child
     */
    private void autoHeight(final View child) {
        if (child != null) {
            new Handler() {
            }.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (child.getBottom() > mvg.getBottom() || mvg.getBottom() - child.getBottom() >= child.getHeight()) {
                        LayoutParams l = mvg.getLayoutParams();
                        l.height = child.getBottom();
                        mvg.setLayoutParams(l);
                    }
                }
            }, 500);
        }
    }

    /**
     * 生成MyViewGroup的子元素
     *
     * @param text
     */
    private void createView1(String text, String number) {

        if (etMess.getText().toString().equals(" ") || etMess.getText().toString().equals("")) {
        } else {
            TextView t = new TextView(this);
            t.setText(text);
            t.setTextColor(Color.BLACK);
            t.setGravity(Gravity.CENTER);
            t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
            t.setHeight(60);
            t.setPadding(2, 0, 2, 0);
            t.setOnClickListener(new MyListener());
            t.setTag(number);
            mvg.addView(t, mvg.getChildCount() - 1);

            ContactBean cb = new ContactBean();
            cb.setDisplayName(text);
            cb.setPhoneNum(number);
            if (null == selectContactList) {
                selectContactList = new ArrayList<ContactBean>();
            }
            selectContactList.add(cb);
            queryListView.setVisibility(View.INVISIBLE);
        }
    }

    private void createView2(String text) {

        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.BLACK);
        t.setGravity(Gravity.CENTER);
        t.setHeight(60);
        t.setPadding(2, 0, 2, 0);
        t.setBackgroundResource(R.drawable.bg_sms_contact_btn);
        t.setOnClickListener(new MyListener());
        t.setTag(text);
        mvg.addView(t, mvg.getChildCount() - 1);
    }

    /**
     * MyViewGroup子元素的事件
     *
     * @author LDM
     */
    private class MyListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            mvg.removeView(v);
            String number = (String) v.getTag();
            for (ContactBean cb : selectContactList) {
                if (cb.getPhoneNum().equals(number)) {
                    selectContactList.remove(cb);
                    break;
                }
            }
            autoHeight(mvg.getChildAt(mvg.getChildCount() - 1));
        }
    }


    /**
     * 数据库异步查询类AsyncQueryHandler
     *
     * @author administrator
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {

                list = new ArrayList<ContactBean>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    ContactBean cb = new ContactBean();
                    cb.setDisplayName(name);
                    cb.setPhoneNum(number);
                    cb.setSortKey(sortKey);
                    cb.setContactId(contactId);
                    cb.setPhotoId(photoId);
                    cb.setLookUpKey(lookUpKey);

                    list.add(cb);
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }
    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new NewSmsAdapter(this);
        adapter.assignment(list);
        queryListView.setAdapter(adapter);
        queryListView.setTextFilterEnabled(true);
        queryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = adapter.getItem(position);
                boolean b = true;
                if (null == selectContactList || selectContactList.size() < 1) {
                } else {
                    for (ContactBean cbean : selectContactList) {
                        if (cbean.getPhoneNum().equals(cb.getPhoneNum())) {
                            b = false;
                            break;
                        }
                    }
                }
                if (b) {
                    etMess.setText(cb.getDisplayName());
                    createView1(etMess.getText().toString().trim(), cb.getPhoneNum());
                    etMess.setText("");
                } else {
                    queryListView.setVisibility(View.INVISIBLE);
                    etMess.setText("");
                }
            }
        });
        queryListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(NewSMSActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }


}
