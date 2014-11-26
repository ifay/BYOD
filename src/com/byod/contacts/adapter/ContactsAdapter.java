package com.byod.contacts.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.byod.R;
import com.byod.bean.ContactBean;
import com.byod.ui.QuickAlphabeticBar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ContactsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ContactBean> mContactList;
    private HashMap<String, Integer> mAlphaIndexer;
    private Context ctx;

    public ContactsAdapter(Context context, List<ContactBean> list, QuickAlphabeticBar alpha) {
        this.ctx = context;
        this.inflater = LayoutInflater.from(context);
        this.mContactList = list;
        this.mAlphaIndexer = new HashMap<String,Integer>();

        for (int i = 0; i < list.size(); i++) {
            String name = getAlpha(list.get(i).getSortKey());
            if (!mAlphaIndexer.containsKey(name)) {
                mAlphaIndexer.put(name, i); // 字母索引, 处位置
            }
        }
        alpha.setAlphaIndexer(mAlphaIndexer);
    }

    @Override
    public int getCount() {
        return mContactList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        mContactList.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contacts_list_item, null);
            holder = new ViewHolder();
            holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactBean cb = mContactList.get(position);
        holder.name.setText(cb.getDisplayName());
        holder.number.setText(cb.getPhoneNum());
        holder.qcb.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));
        if (0 != cb.getPhotoId()) {
            Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, cb.getContactId());
            InputStream input = Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
            Bitmap contactPhoto = BitmapFactory.decodeStream(input);
            holder.qcb.setImageBitmap(contactPhoto);
        }
        String currentStr = getAlpha(cb.getSortKey());
        String previewStr = (position - 1) >= 0 ? getAlpha(mContactList.get(position - 1).getSortKey()) : " ";
        if (!previewStr.equals(currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        // 头像
        QuickContactBadge qcb;
        // 字母分隔栏
        TextView alpha;
        TextView name;
        TextView number;
    }

    private String getAlpha(String str) {
        if (TextUtils.isEmpty(str)) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        if (Character.isLetter(c)) {
            return String.valueOf(Character.toUpperCase(c));
        } else {
            return "#";
        }
    }
}
