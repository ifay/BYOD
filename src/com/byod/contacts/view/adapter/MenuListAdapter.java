package com.byod.contacts.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.byod.R;
import com.byod.contacts.bean.GroupBean;
import com.google.gson.Gson;

import java.util.List;

public class MenuListAdapter extends BaseAdapter {

    private Activity context;
    private int itemCount;
    private LayoutInflater listInflater;
    private boolean isPressed[];
    private int pressedId;
    private List<GroupBean> list;

    /*一个menu item中包含一个imageView和一个TextView*/
    public final class ListItemsView {
        public TextView menuText;
    }

    public MenuListAdapter(Activity context, List<GroupBean> list) {
        this.list = list;
        this.context = context;
        this.init();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemsView listItemsView;
        if (convertView == null) {
            listItemsView = new ListItemsView();
            convertView = this.listInflater.inflate(R.layout.menu_list_item, null);
            listItemsView.menuText = (TextView) convertView.findViewById(R.id.menuText);
            convertView.setTag(listItemsView);
        } else {
            listItemsView = (ListItemsView) convertView.getTag();
        }

        listItemsView.menuText.setText(list.get(position).getName());

        if (this.isPressed[position] == true)
            convertView.setBackgroundResource(R.drawable.menu_item_bg_sel);
        else
            convertView.setBackgroundResource(R.drawable.menu_item_bg_sel_off);

        addViewListener(convertView, list.get(position), position);
//		convertView.setOnClickListener(new OnClickListener(){
//			public void onClick(View view) {
//				changeState(po);
////				gotoActivity(po);
//				notifyDataSetInvalidated();
//				new Handler().post(new Runnable(){
//					public void run() {
//						
//					}
//				});
//			}
//		});
        return convertView;
    }

    private void addViewListener(View view, final GroupBean gb, final int position) {

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeState(position);
//				gotoActivity(po);

                Gson gson = new Gson();
                String bean = gson.toJson(gb);
                Intent it1 = new Intent();
                it1.setAction("SET_DEFAULT_SIG");
                it1.putExtra("groupbean", bean);
                context.sendBroadcast(it1);


                notifyDataSetInvalidated();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }


//	private void gotoActivity(int position){
//		Intent intent = new Intent();
//		switch(position){
//		case 0:
//			
//			if(this.pressedId == 0){
//				MainActivity activity = (MainActivity)context;
//				activity.getScrollView().clickMenuBtn(context);
//			}
//			else{
//				intent.setClass(context, MainActivity.class);
//				context.startActivity(intent);
//				context.overridePendingTransition(R.anim.push_in,R.anim.push_out);
//				context.finish();
//			}
//			System.out.println("case11111111");
//			break;
//		/*----------------------------------------------------*/	
//		case 1:
////			if(this.pressedId == 1){
////				CAROLPageActivity activity = (CAROLPageActivity)context;
////				activity.getScrollView().clickMenuBtn(context);
////			}
////			else{
////				intent.setClass(context, CAROLPageActivity.class);
////				context.startActivity(intent);
////				context.overridePendingTransition(R.anim.push_in,R.anim.push_out);
////				context.finish();
////			}
//			System.out.println("case22222222");
//			break;
//		/*----------------------------------------------------*/
//		default:
//			System.out.println("case33333333");
////			intent.setClass(context, WebPageActivity.class);
////			context.startActivity(intent);
//		}
//	}

    private void changeState(int position) {

        for (int i = 0; i < this.itemCount; i++) {
            isPressed[i] = false;
        }
        isPressed[position] = true;
    }

    private void init() {
        this.itemCount = this.list.size();
        this.isPressed = new boolean[this.itemCount];
        for (int i = 0; i < this.itemCount; i++) {
            this.isPressed[i] = false;
        }
        this.isPressed[this.pressedId] = true;
        this.listInflater = LayoutInflater.from(context);
    }
}
