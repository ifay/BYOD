package com.byod.contacts.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.byod.R;
import com.byod.contacts.data.ContactsAsyncQueryFactory;
import com.byod.data.IAsyncQuery;
import com.byod.data.IAsyncQueryHandler;
import com.byod.utils.ToPinYin;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import static com.byod.data.db.DatabaseHelper.ContactsColumns;

public class AddPeopleActivity extends Activity implements  IAsyncQueryHandler{
    private static String[] itemActions = new String[]{"拨打电话", "发送短信", "查看详细", "删除", "修改头像"};

    private EditText mName;
    private EditText mNumber;
    private EditText mEmail;
    private ImageView mIcon;
    private Button mBack;
    private Button mAdd;
    ContactsAsyncQueryFactory mAsyncQueryFactory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_addnew);

        mName = (EditText)findViewById(R.id.name);
        mNumber = (EditText)findViewById(R.id.number);
        mEmail = (EditText)findViewById(R.id.email);
        mIcon = (ImageView)findViewById(R.id.icon);
        mBack = (Button)findViewById(R.id.btn_return);
        mAdd = (Button)findViewById(R.id.add);

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(ContactsColumns.DISPLAY_NAME, mName.getEditableText().toString());
                try {
                    values.put(ContactsColumns.SORT_KEY, ToPinYin.getPinYin(mName.getEditableText().toString()));
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    values.put(ContactsColumns.SORT_KEY, mName.getEditableText().toString());
                }
                values.put(ContactsColumns.PHONE, mNumber.getEditableText().toString().replaceAll("\\s", ""));
                values.put(ContactsColumns.EMAIL, mEmail.getEditableText().toString().replaceAll("\\s", ""));

                IAsyncQuery query = mAsyncQueryFactory.getLocalAsyncQuery();
                query.startInsert(values);
            }
        });

        mAsyncQueryFactory = new ContactsAsyncQueryFactory(this, this);
    }

    /**
     * 查询结束的回调函数
     */
    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        finish();
    }
}
