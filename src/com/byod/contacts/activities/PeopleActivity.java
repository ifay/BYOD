package com.byod.contacts.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.byod.R;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.uitl.BaseIntentUtil;
import com.byod.contacts.adapter.ContactsAdapter;
import com.byod.sms.activities.MessageBoxList;
import com.byod.ui.QuickAlphabeticBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeopleActivity extends Activity {
    private static String[] itemActions = new String[]{"拨打电话", "发送短信", "查看详细", "删除", "修改头像"};

    private ContactsAdapter adapter;
    private ListView personList;
    private List<ContactBean> list;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;
    private Button addContactBtn;

    private Map<Integer, ContactBean> contactIdMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_page);

        personList = (ListView) findViewById(R.id.acbuwa_list);
        alpha = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);
        addContactBtn = (Button) findViewById(R.id.addContactBtn);

        addContactBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到添加联系人界面，存到自己数据库
            }
        });

        // TODO 数据库操作分离出去
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
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
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

        /**
         * 查询结束的回调函数
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, ContactBean>();

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

                    if (contactIdMap.containsKey(contactId)) {
                        continue;
                    } else {
                        ContactBean cb = new ContactBean();
                        cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//						cb.setPhoneNum(number.substring(3));
//					} else {
                        cb.setPhoneNum(number);
//					}
                        cb.setSortKey(sortKey);
                        cb.setContactId(contactId);
                        cb.setPhotoId(photoId);
                        cb.setLookUpKey(lookUpKey);
                        list.add(cb);

                        contactIdMap.put(contactId, cb);
                    }
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }
    }

    private void setAdapter(List<ContactBean> list) {
        adapter = new ContactsAdapter(this, list, alpha);
        personList.setAdapter(adapter);
        alpha.init(PeopleActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                showContactDialog(itemActions, cb, position);
            }
        });
    }

    //联系人弹出页
    private void showContactDialog(final String[] arg, final ContactBean cb, final int position) {
        new AlertDialog.Builder(this).setTitle(cb.getDisplayName()).setItems(arg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = null;
                        switch (which) {
                            case 0://打电话
                                String toPhone = cb.getPhoneNum();
                                uri = Uri.parse("tel:" + toPhone);
                                Intent it = new Intent(Intent.ACTION_CALL, uri);
                                startActivity(it);
                                break;
                            case 1://发短息
                                String threadId = getSMSThreadId(cb.getPhoneNum());
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("phoneNumber", cb.getPhoneNum());
                                map.put("threadId", threadId);
                                BaseIntentUtil.intentSysDefault(PeopleActivity.this, MessageBoxList.class, map);
                                break;
                            case 2:// 查看详细       修改联系人资料
                                uri = ContactsContract.Contacts.CONTENT_URI;
                                Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
                                Intent intent2 = new Intent();
                                intent2.setAction(Intent.ACTION_VIEW);
                                intent2.setData(personUri);
                                startActivity(intent2);
                                break;
                            case 5:// 删除
                                showDelete(cb.getContactId(), position);
                                break;
                            case 6://修改头像
                                // TODO
                                break;
                        }
                    }
                }).show();
    }

    // 删除联系人方法
    private void showDelete(final int contactsID, final int position) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("是否删除此联系人")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //源码删除
                        Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
                        Uri lookupUri = ContactsContract.Contacts.getLookupUri(PeopleActivity.this.getContentResolver(), deleteUri);
                        if (lookupUri != Uri.EMPTY) {
                            PeopleActivity.this.getContentResolver().delete(deleteUri, null, null);
                        }
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PeopleActivity.this, "该联系人已经被删除.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }

    public static String[] SMS_COLUMNS = new String[]{
            "thread_id"
    };

    private String getSMSThreadId(String adddress) {
        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(Uri.parse("content://sms"), SMS_COLUMNS, " address like '%" + adddress + "%' ", null, null);
        String threadId = "";
        if (cursor == null || cursor.getCount() > 0) {
            cursor.moveToFirst();
            threadId = cursor.getString(0);
            cursor.close();
            return threadId;
        } else {
            cursor.close();
            return threadId;
        }
    }
}
