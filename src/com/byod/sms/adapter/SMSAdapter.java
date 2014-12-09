package com.byod.sms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.byod.R;
import com.byod.bean.SMSBean;
import com.byod.data.db.ContactsContentProvider;
import com.byod.data.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SMSBean> list;
    private Context context;
    private Date d;
    private SimpleDateFormat sdf;

    public SMSAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<SMSBean>();
        this.context = context;
        this.d = new Date();
        this.sdf = new SimpleDateFormat("MM/dd HH:mm");
    }

    public void assignment(List<SMSBean> list) {
        Log.d("SMSAdapter", "list Size: " + list.size());
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void add(SMSBean bean) {
        list.add(bean);
    }

    public void remove(int position) {
        list.remove(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SMSBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sms_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getPersonName(list.get(position).getAddress()));

        this.d.setTime(list.get(position).getDate());
        holder.date.setText(this.sdf.format(d));

        return convertView;
    }

    public final class ViewHolder {
        public TextView name;
        public TextView date;
    }

    public String getPersonName(String number) {
        number = number.replaceAll("\\s", "");
        String[] projection = {DatabaseHelper.ContactsColumns.DISPLAY_NAME,};
        Cursor cursor = this.context.getContentResolver().query(
                ContactsContentProvider.CONTACTS_URI,
                projection,
                DatabaseHelper.ContactsColumns.PHONE + " = '" + number + "'",
                null,
                null);
        if (cursor == null) {
            return number;
        }
        String name = number;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ContactsColumns.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }
}
