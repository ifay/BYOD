package com.byod.sms.activities;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.R;
import com.byod.contacts.bean.MessageBean;
import com.byod.sms.adapter.MessageBoxListAdapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageBoxList extends Activity {

    private ListView talkView;
    private List<MessageBean> list = null;
    private Button fasong;
    private Button btn_return;
    private Button btn_call;
    private EditText neirong;
    private SimpleDateFormat sdf;
    private AsyncQueryHandler asyncQuery;
    private String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_messageboxlist);

        btn_return = (Button) findViewById(R.id.btn_return);
        btn_call = (Button) findViewById(R.id.btn_call);
        fasong = (Button) findViewById(R.id.fasong);
        neirong = (EditText) findViewById(R.id.neirong);

        String thread = getIntent().getStringExtra("threadId");
        address = getIntent().getStringExtra("phoneNumber");
        TextView tv = (TextView) findViewById(R.id.topbar_title);
        tv.setText(getPersonName(address));

        sdf = new SimpleDateFormat("MM-dd HH:mm");

        init(thread);

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
                ContentValues values = new ContentValues();
                values.put("address", address);
                values.put("body", nei);
                getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                Toast.makeText(MessageBoxList.this, nei, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(String thread) {

        asyncQuery = new MyAsyncQueryHandler(getContentResolver());

        talkView = (ListView) findViewById(R.id.list);
        list = new ArrayList<MessageBean>();

        Uri uri = Uri.parse("content://sms");
        String[] projection = new String[]{
                "date",
                "address",
                "person",
                "body",
                "type"
        };// 查询的列
        asyncQuery.startQuery(0, null, uri, projection, "thread_id = " + thread, null,
                "date asc");
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
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
                    if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
                        MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
                                date,
                                cursor.getString(cursor.getColumnIndex("body")),
                                R.layout.list_say_he_item);
                        list.add(d);
                    } else {
                        MessageBean d = new MessageBean(cursor.getString(cursor.getColumnIndex("address")),
                                date,
                                cursor.getString(cursor.getColumnIndex("body")),
                                R.layout.list_say_me_item);
                        list.add(d);
                    }
                }
                if (list.size() > 0) {
                    talkView.setAdapter(new MessageBoxListAdapter(MessageBoxList.this, list));
                    talkView.setDivider(null);
                    talkView.setSelection(list.size());
                } else {
                    Toast.makeText(MessageBoxList.this, "没有短信进行操作", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getPersonName(String number) {
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,};
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",
                null,
                null);
        if (cursor == null) {
            return number;
        }
        String name = number;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }

}
