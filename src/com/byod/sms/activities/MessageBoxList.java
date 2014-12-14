package com.byod.sms.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.R;
import com.byod.bean.MessageBean;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryFactory;
import com.byod.data.IAsyncQueryHandler;
import com.byod.data.db.ContactsContentProvider;
import com.byod.sms.adapter.MessageBoxListAdapter;
import com.byod.sms.data.SMSAsyncQueryFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.byod.data.db.DatabaseHelper.ContactsColumns;
import static com.byod.data.db.DatabaseHelper.SMSColumns.ADDRESS;
import static com.byod.data.db.DatabaseHelper.SMSColumns.BODY;
import static com.byod.data.db.DatabaseHelper.SMSColumns.DATE;
import static com.byod.data.db.DatabaseHelper.SMSColumns.MESSAGE_TYPE_SENT;
import static com.byod.data.db.DatabaseHelper.SMSColumns.READ;
import static com.byod.data.db.DatabaseHelper.SMSColumns.TYPE;

public class MessageBoxList extends Activity implements IAsyncQueryHandler {

    private ListView talkView;
    private List<MessageBean> list = null;
    private Button fasong;
    private Button btn_return;
    private Button btn_call;
    private EditText neirong;
    private SimpleDateFormat sdf;
    private String address;
    private String thread;
    private IAsyncQueryFactory mAsyncQueryFactory;
    MessageBoxListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_messageboxlist);

        btn_return = (Button) findViewById(R.id.btn_return);
        btn_call = (Button) findViewById(R.id.btn_call);
        fasong = (Button) findViewById(R.id.fasong);
        neirong = (EditText) findViewById(R.id.neirong);

        thread = getIntent().getStringExtra("threadId");
        address = getIntent().getStringExtra("phoneNumber");
        TextView tv = (TextView) findViewById(R.id.topbar_title);
        tv.setText(getPersonName(address));

        sdf = new SimpleDateFormat("MM-dd HH:mm");

        talkView = (ListView) findViewById(R.id.list);
        list = new ArrayList<MessageBean>();

        mAsyncQueryFactory = new SMSAsyncQueryFactory(this, this);

        btn_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBoxList.this.setResult(RESULT_OK);
                MessageBoxList.this.finish();
            }
        });
        btn_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + address);
                Intent it = new Intent(Intent.ACTION_CALL, uri);
                startActivity(it);
            }
        });


        fasong.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String nei = neirong.getText().toString();

                Log.d("NewSMSActivity", "Send: " + address);
//                        ContentValues values = new ContentValues();
//                        values.put("address", cb.getPhoneNum());
//                        values.put("body", nei);
//                        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
//                        Toast.makeText(NewSMSActivity.this, nei, Toast.LENGTH_SHORT).show();
                //直接调用短信接口发短信
                SmsManager smsManager = SmsManager.getDefault();
                List<String> divideContents = smsManager.divideMessage(nei);
                for (String text : divideContents) {
                    smsManager.sendTextMessage(address, null, text, null, null);
                }

                ContentValues values = new ContentValues();
                values.put(BODY, nei);
                values.put(ADDRESS, address);
                values.put(TYPE, MESSAGE_TYPE_SENT);
                values.put(DATE, System.currentTimeMillis());
                values.put(READ, 1);

                IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
                query.startInsert(values);
//                ContentValues values = new ContentValues();
//                values.put("address", address);
//                values.put("body", nei);
//                getContentResolver().insert(Uri.parse("content://sms/sent"), values);
//                Toast.makeText(MessageBoxList.this, nei, Toast.LENGTH_SHORT).show();
                query.startQuery();
                neirong.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        list = new ArrayList<MessageBean>();
//        MessageBean sb = new MessageBean("15311448992",
//                sdf.format(new Date(System.currentTimeMillis())),
//                "Test test, test.",
//                R.layout.list_say_he_item);
//        sb.setName("小张");
//        list.add(sb);
//        mAdapter = new MessageBoxListAdapter(MessageBoxList.this, list);
//        talkView.setAdapter(mAdapter);
//        talkView.setDivider(null);
//        talkView.setSelection(list.size());
        IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
        query.startQuery();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String phone = cursor.getString(cursor.getColumnIndex(ADDRESS));
                if (phone.equals(address)) {
                    String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex(DATE))));
                    if (cursor.getInt(cursor.getColumnIndex(TYPE)) == 1) {
                        MessageBean d = new MessageBean(phone,
                                date,
                                cursor.getString(cursor.getColumnIndex(BODY)),
                                R.layout.list_say_he_item);
                        list.add(d);
                    } else {
                        MessageBean d = new MessageBean(phone,
                                date,
                                cursor.getString(cursor.getColumnIndex(BODY)),
                                R.layout.list_say_me_item);
                        list.add(d);
                    }
                }
            }
            cursor.close();
            if (list.size() > 0) {
                mAdapter = new MessageBoxListAdapter(MessageBoxList.this, list);
                talkView.setAdapter(mAdapter);
                talkView.setDivider(null);
                talkView.setSelection(list.size());
            } else {
                Toast.makeText(MessageBoxList.this, "没有短信进行操作", Toast.LENGTH_SHORT).show();
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

    public String getPersonName(String number) {
        number = number.replaceAll("\\s", "");
        String[] projection = {ContactsColumns.DISPLAY_NAME,};
        Cursor cursor = this.getContentResolver().query(
                ContactsContentProvider.CONTACTS_URI,
                projection,
                ContactsColumns.PHONE + " = '" + number + "'",
                null,
                null);
        if (cursor == null) {
            return number;
        }
        String name = number;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(cursor.getColumnIndex(ContactsColumns.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }
}
