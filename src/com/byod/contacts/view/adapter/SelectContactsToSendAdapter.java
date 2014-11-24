package com.byod.contacts.view.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.byod.R;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.view.ui.QuickAlphabeticBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SelectContactsToSendAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ContactBean> list;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private Context ctx;
    public static Map<Integer, Boolean> isSelected;
    public int a, b, c;


    public SelectContactsToSendAdapter(Context context, List<ContactBean> list, QuickAlphabeticBar alpha) {

        this.ctx = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.alphaIndexer = new HashMap<String, Integer>();
        this.sections = new String[list.size()];
        isSelected = new HashMap<Integer, Boolean>();

        for (int i = 0; i < list.size(); i++) {

            isSelected.put(i, false);
            if (list.get(i).getSelected() == 1) {
                isSelected.put(i, true);
            }

            String name = getAlpha(list.get(i).getSortKey());
            if (!alphaIndexer.containsKey(name)) {
                alphaIndexer.put(name, i);
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);

        alpha.setAlphaIndexer(alphaIndexer);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        list.remove(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.select_contact_to_send_list_item, null);


            holder = new ViewHolder();
            holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.check = (ImageView) convertView.findViewById(R.id.check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactBean cb = list.get(position);
        String name = cb.getDisplayName();
        String number = cb.getPhoneNum();
        holder.name.setText(name);
        holder.number.setText(number);
        holder.qcb.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));
        if (0 == cb.getPhotoId()) {


            holder.qcb.setImageResource(R.drawable.andriod0001);
        } else {
            Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, cb.getContactId());
            InputStream input = Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
            Bitmap contactPhoto = BitmapFactory.decodeStream(input);
            holder.qcb.setImageBitmap(contactPhoto);
        }
        String currentStr = getAlpha(cb.getSortKey());
        String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";

        if (isSelected.get(position)) {
            holder.check.setImageResource(R.drawable.ic_checkbox_checked);
        } else {
            holder.check.setImageResource(R.drawable.ic_checkbox_unchecked);
        }

        if (!previewStr.equals(currentStr)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(currentStr);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        QuickContactBadge qcb;
        TextView alpha;
        TextView name;
        TextView number;
        ImageView check;
    }

    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }
}
