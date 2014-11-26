package com.byod.sms.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byod.R;
import com.byod.bean.MessageBean;

import java.util.List;

public class MessageBoxListAdapter extends BaseAdapter {

    private List<MessageBean> mbList;
    private Context ctx;
    private LinearLayout layout_father;
    private LayoutInflater vi;
    private LinearLayout layout_child;
    private TextView tvDate;
    private TextView tvText;

    public MessageBoxListAdapter(Context context, List<MessageBean> coll) {
        ctx = context;
        vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mbList = coll;
    }

    @Override
    public int getCount() {
        return mbList.size();
    }

    @Override
    public Object getItem(int position) {
        return mbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageBean mb = mbList.get(position);
        int itemLayout = mb.getLayoutID();
        layout_father = new LinearLayout(ctx);
        vi.inflate(itemLayout, layout_father, true);

        layout_father.setBackgroundColor(Color.TRANSPARENT);
        layout_child = (LinearLayout) layout_father.findViewById(R.id.layout_bj);

        tvText = (TextView) layout_father.findViewById(R.id.messagedetail_row_text);
        tvText.setText(mb.getText());

        tvDate = (TextView) layout_father.findViewById(R.id.messagedetail_row_date);
        tvDate.setText(mb.getDate());

        addListener(tvText, tvDate, layout_child, mb);

        return layout_father;
    }

    public void addListener(final TextView tvText, final TextView tvDate, LinearLayout layout_bj, final MessageBean mb) {

        layout_bj.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layout_bj.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tvText.setTextColor(0xffffffff);
                showListDialog(newtan, mb);
                return true;
            }
        });

        layout_bj.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:


                    case MotionEvent.ACTION_MOVE:
                        tvText.setTextColor(0xffffffff);
                        break;

                    default:
                        tvText.setTextColor(Color.BLACK);
                        break;
                }
                return false;
            }
        });
    }


    private String[] newtan = new String[]{"转发", "复制文本内容", "删除", "查看信息详情"};

    private void showListDialog(final String[] arg, final MessageBean mb) {
        new AlertDialog.Builder(ctx).setTitle("信息选项").setItems(arg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                break;

                            case 1:
                                ClipboardManager cmb = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText(mb.getText());
                                break;
                            case 2:

                                break;
                            case 3:
                                break;
                        }
                        ;
                    }
                }).show();
    }
}
