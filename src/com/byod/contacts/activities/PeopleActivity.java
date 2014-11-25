package com.byod.contacts.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Groups;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.byod.R;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.bean.GroupBean;
import com.byod.contacts.uitl.BaseIntentUtil;
import com.byod.contacts.view.adapter.ContactHomeAdapter;
import com.byod.contacts.view.adapter.MenuListAdapter;
import com.byod.contacts.view.other.SizeCallBackForMenu;
import com.byod.contacts.view.sms.MessageBoxList;
import com.byod.contacts.view.ui.Addpic;
import com.byod.contacts.view.ui.MenuHorizontalScrollView;
import com.byod.contacts.view.ui.QuickAlphabeticBar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PeopleActivity extends Activity {
    public static int positioncommon;
    private MenuHorizontalScrollView scrollView;
    private ListView menuList;
    private View acbuwaPage;
    private Button menuBtn;
    private MenuListAdapter menuListAdapter;
    private View[] children;
    private LayoutInflater inflater;


    private ContactHomeAdapter adapter;
    private ListView personList;
    private List<ContactBean> list;
    private AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;
    private Button addContactBtn;

    private Map<Integer, ContactBean> contactIdMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_scroll_view);
        scrollView = (MenuHorizontalScrollView) findViewById(R.id.mScrollView);
        menuListAdapter = new MenuListAdapter(this, queryGroup());
        menuList = (ListView) findViewById(R.id.menuList);
        menuList.setAdapter(menuListAdapter);


        acbuwaPage = inflater.inflate(R.layout.home_contact_page, null);
        menuBtn = (Button) this.acbuwaPage.findViewById(R.id.menuBtn);

        personList = (ListView) this.acbuwaPage.findViewById(R.id.acbuwa_list);

        alpha = (QuickAlphabeticBar) this.acbuwaPage.findViewById(R.id.fast_scroller);
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        init();

        menuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.clickMenuBtn(PeopleActivity.this);
            }
        });


        View leftView = new View(this);
        leftView.setBackgroundColor(Color.TRANSPARENT);
        children = new View[]{leftView, acbuwaPage};
        scrollView.initViews(children, new SizeCallBackForMenu(this.menuBtn), this.menuList);
        scrollView.setMenuBtn(this.menuBtn);

        addContactBtn = (Button) findViewById(R.id.addContactBtn);
        addContactBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri insertUri = ContactsContract.Contacts.CONTENT_URI;

                Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
                startActivityForResult(intent, 1008);
            }
        });

        startReceiver1();


    }

    private void init() {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MenuHorizontalScrollView.menuOut == true)
                this.scrollView.clickMenuBtn(PeopleActivity.this);
            else
                this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        adapter = new ContactHomeAdapter(this, list, alpha);
        personList.setAdapter(adapter);
        alpha.init(PeopleActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                showContactDialog(lianxiren1, cb, position);
                positioncommon = position;
            }
        });
    }


    private String[] lianxiren1 = new String[]{"拨打电话", "发送短信", "查看详细", "移动分组", "移出群组", "删除", "上传头像"};

    //群组联系人弹出页
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

                            case 3:// 移动分组

                                //					Intent intent3 = null;
                                //					intent3 = new Intent();
                                //					intent3.setClass(ContactHome.this, GroupChoose.class);
                                //					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //					intent3.putExtra("联系人", contactsID);
                                //					Log.e("contactsID", "contactsID---"+contactsID);
                                //					ContactHome.this.startActivity(intent3);
                                break;

                            case 4:// 移出群组

                                //					moveOutGroup(getRaw_contact_id(contactsID),Integer.parseInt(qzID));
                                break;

                            case 5:// 删除

                                showDelete(cb.getContactId(), position);
                                break;
                            case 6://上传头像
                                Intent shangchuan = new Intent();
                                shangchuan.setClass(PeopleActivity.this, Addpic.class);
                                startActivityForResult(shangchuan, 1000);

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

    /**
     * 查询所有群组
     * 返回值List<ContactGroup>
     */
    public List<GroupBean> queryGroup() {

        List<GroupBean> list = new ArrayList<GroupBean>();

        GroupBean cg_all = new GroupBean();
        cg_all.setId(0);
        cg_all.setName("全部");
        list.add(cg_all);

        Cursor cur = getContentResolver().query(Groups.CONTENT_URI, null, null, null, null);
        for (cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()) {
            if (null != cur.getString(cur.getColumnIndex(Groups.TITLE)) && (!"".equals(cur.getString(cur.getColumnIndex(Groups.TITLE))))) {
                GroupBean cg = new GroupBean();
                cg.setId(cur.getInt(cur.getColumnIndex(BaseColumns._ID)));
                cg.setName(cur.getString(cur.getColumnIndex(Groups.TITLE)));
                list.add(cg);
            }
        }
        cur.close();
        return list;
    }

    private void queryGroupMember(GroupBean gb) {

        String[] RAW_PROJECTION = new String[]{ContactsContract.Data.RAW_CONTACT_ID};

        Cursor cur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
                ContactsContract.Data.MIMETYPE + " = '" + GroupMembership.CONTENT_ITEM_TYPE
                        + "' AND " + ContactsContract.Data.DATA1 + "=" + gb.getId(),
                null,
                "data1 asc");

        StringBuilder inSelectionBff = new StringBuilder().append(BaseColumns._ID + " IN ( 0");
        while (cur.moveToNext()) {
            inSelectionBff.append(',').append(cur.getLong(0));
        }
        cur.close();
        inSelectionBff.append(')');

        Cursor contactIdCursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.CONTACT_ID}, inSelectionBff.toString(), null, ContactsContract.Contacts.DISPLAY_NAME + "  COLLATE LOCALIZED asc ");
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        while (contactIdCursor.moveToNext()) {
            map.put(contactIdCursor.getInt(0), 1);
        }
        contactIdCursor.close();

        Set<Integer> set = map.keySet();
        Iterator<Integer> iter = set.iterator();
        List<ContactBean> list = new ArrayList<ContactBean>();
        while (iter.hasNext()) {
            Integer key = iter.next();
            list.add(queryMemberOfGroup(key));
        }
        setAdapter(list);
    }

    private ContactBean queryMemberOfGroup(int id) {

        ContactBean cb = null;

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
        Cursor cursor = getContentResolver().query(uri, projection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
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

                cb = new ContactBean();
                cb.setDisplayName(name);
//				if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//					cb.setPhoneNum(number.substring(3));
//				} else {
                cb.setPhoneNum(number);
//				}
                cb.setSortKey(sortKey);
                cb.setContactId(contactId);
                cb.setPhotoId(photoId);
                cb.setLookUpKey(lookUpKey);
            }
        }
        cursor.close();
        return cb;
    }


    /**
     * 数据库异步查询类AsyncQueryHandler
     *
     * @author administrator
     */
    private class GroupQueryHandler extends AsyncQueryHandler {

        public GroupQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 查询结束的回调函数
         */
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                list = new ArrayList<ContactBean>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    ContentValues cv = new ContentValues();
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    ContactBean cb = new ContactBean();
                    cb.setDisplayName(name);
                    if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
                        cb.setPhoneNum(number.substring(3));
                    } else {
                        cb.setPhoneNum(number);
                    }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (1008 == requestCode) {
            init();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopReceiver1();
    }

    private String ACTION1 = "SET_DEFAULT_SIG";
    private BaseReceiver1 receiver1 = null;

    /**
     * 打开接收器
     */
    private void startReceiver1() {
        if (null == receiver1) {
            IntentFilter localIntentFilter = new IntentFilter(ACTION1);
            receiver1 = new BaseReceiver1();
            this.registerReceiver(receiver1, localIntentFilter);
        }
    }

    /**
     * 关闭接收器
     */
    private void stopReceiver1() {
        if (null != receiver1)
            unregisterReceiver(receiver1);
    }

    public class BaseReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION1)) {

                String str_bean = intent.getStringExtra("groupbean");
                Gson gson = new Gson();
                GroupBean gb = gson.fromJson(str_bean, GroupBean.class);
                if (gb.getId() == 0) {

                    init();
                } else {

                    queryGroupMember(gb);
                }
            }
        }
    }
}
