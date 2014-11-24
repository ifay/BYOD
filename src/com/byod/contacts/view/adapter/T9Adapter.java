package com.byod.contacts.view.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.byod.R;
import com.byod.contacts.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

public class T9Adapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;
    private List<ContactBean> list;
    private List<ContactBean> allContactList;
    private Context context;
    private String filterNum;

    public T9Adapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.list = new ArrayList<ContactBean>();
        this.context = context;
    }

    public void assignment(List<ContactBean> list) {
        this.allContactList = list;
        this.list = this.allContactList;
    }

    public void add(ContactBean bean) {
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
    public ContactBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_t9_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.pinyin = (TextView) convertView.findViewById(R.id.pinyin);
            holder.number = (TextView) convertView.findViewById(R.id.number);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getDisplayName());
        String formattedNumber = list.get(position).getPinyin();

        if (null == filterNum || "".equals(filterNum)) {
//			holder.pinyin.setVisibility(View.INVISIBLE);
            holder.number.setText(list.get(position).getPhoneNum());
        } else {
//			holder.pinyin.setVisibility(View.VISIBLE);
            holder.number.setText(Html.fromHtml(list.get(position).getPhoneNum().replace(filterNum, "<font color='#cc0000'>" + filterNum + "</font>")));
            if (!TextUtils.isEmpty(filterNum)) {
                for (int i = 0; i < filterNum.length(); i++) {
                    char c = filterNum.charAt(i);
                    if (TextUtils.isDigitsOnly(String.valueOf(c))) {
                        char[] zms = digit2Char(Integer.parseInt(c + ""));
                        if (zms != null) {
                            for (char c1 : zms) {
                                formattedNumber = formattedNumber.replaceAll(String.valueOf(c1).toUpperCase(), "%%" + String.valueOf(c1).toUpperCase() + "@@");
                            }
                            formattedNumber = formattedNumber.replaceAll("%%", "<font color='#cc0000'>");
                            formattedNumber = formattedNumber.replaceAll("@@", "</font>");
                        }
                    }
                }
                holder.pinyin.setText(Html.fromHtml(formattedNumber));
            }
        }

        convertView.setTag(holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView name;
        public TextView pinyin;
        public TextView number;
    }

    public char[] digit2Char(int digit) {
        char[] cs = null;
        switch (digit) {
            case 0:
                cs = new char[]{};
                break;
            case 1:
                break;
            case 2:
                cs = new char[]{'a', 'b', 'c'};
                break;
            case 3:
                cs = new char[]{'d', 'e', 'f'};
                break;
            case 4:
                cs = new char[]{'g', 'h', 'i'};
                break;
            case 5:
                cs = new char[]{'j', 'k', 'l'};
                break;
            case 6:
                cs = new char[]{'m', 'n', 'o'};
                break;
            case 7:
                cs = new char[]{'p', 'q', 'r', 's'};
                break;
            case 8:
                cs = new char[]{'t', 'u', 'v'};
                break;
            case 9:
                cs = new char[]{'w', 'x', 'y', 'z'};
                break;
        }
        return cs;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<ContactBean>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence s) {
                String str = s.toString();
                filterNum = str;
                FilterResults results = new FilterResults();
                ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
                if (allContactList != null && allContactList.size() != 0) {
                    for (ContactBean cb : allContactList) {
                        if (cb.getFormattedNumber().indexOf(str) >= 0 || cb.getPhoneNum().indexOf(str) > -1) {
                            contactList.add(cb);
                        }
                    }
                }
                results.values = contactList;
                results.count = contactList.size();
                return results;
            }
        };
        return filter;
    }
}
