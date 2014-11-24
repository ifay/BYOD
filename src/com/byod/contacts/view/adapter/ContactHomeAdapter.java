package com.byod.contacts.view.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.byod.R;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.view.ui.Addpic;
import com.byod.contacts.view.ui.QuickAlphabeticBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ContactHomeAdapter extends BaseAdapter{
	
	private LayoutInflater inflater;
	private List<ContactBean> list;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Context ctx;
	public boolean[] abcd;
	public int a,b,c;
	private  int[] images 
	= new int[]{R.drawable.andriod0001
,R.drawable.andriod0001, R.drawable.andriod0002,R.drawable.andriod0003
,R.drawable.andriod0004,R.drawable.andriod0005,R.drawable.andriod0006
,R.drawable.andriod0007,R.drawable.andriod0008,R.drawable.andriod0009
,R.drawable.andriod0010,R.drawable.andriod0011,R.drawable.andriod0012
,R.drawable.andriod0013,R.drawable.andriod0014,R.drawable.andriod0015
,R.drawable.andriod0016,R.drawable.andriod0017,R.drawable.andriod0019
,R.drawable.andriod0020,R.drawable.andriod0021,R.drawable.andriod0022
,R.drawable.andriod0023,R.drawable.andriod0024,R.drawable.andriod0029
,R.drawable.andriod0026,R.drawable.andriod0027,R.drawable.andriod0028
,R.drawable.andriod0030,R.drawable.andriod0031,R.drawable.andriod0032,
R.drawable.andriod0033,R.drawable.andriod0034,R.drawable.andriod0035,
R.drawable.andriod0036,R.drawable.andriod0037,R.drawable.andriod0038,
R.drawable.andriod0039,R.drawable.andriod0040,R.drawable.andriod0041,
R.drawable.andriod0042};
	public ContactHomeAdapter(Context context, List<ContactBean> list, QuickAlphabeticBar alpha) {
		
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list; 
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];
		
		
		String test=Integer.toString(a);
		Log.v("in",test);
		for (int i =0; i <list.size(); i++) {
			String name = getAlpha(list.get(i).getSortKey());
			if(!alphaIndexer.containsKey(name)){ 
				alphaIndexer.put(name, i);
			}
		}
		abcd=new boolean[10000];
		for(int i=0;i<10000;i++)
		{
			abcd[i]=false;
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
	
	public void remove(int position){
		list.remove(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_home_list_item, null);
			holder = new ViewHolder();
			holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView
					.findViewById(R.id.number);
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
		if(0 == cb.getPhotoId()){
			
			
		
		
		 c= Addpic.tt;
		 boolean fast=Addpic.imageChanged;
		 String str=String.valueOf(position);
		 Log.v("tt",str);
	       if(abcd[position])
	       {
	    	   
	       }
	       else
	       {
			 if(position==Addpic.pp)
			 {
				 if(fast)
				 {
					 holder.qcb.setImageResource(images[c]);
					 abcd[position]=true;
					 //fast=false;
					 //Addpic.imageChanged=false;
				 }
				 else
				 {
					
					 holder.qcb.setImageResource(R.drawable.andriod0001);
				 }
			 }
			
		 else
		 {
			 holder.qcb.setImageResource(R.drawable.andriod0001);
		 }
	       }
			
		}else{
			Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, cb.getContactId());
			InputStream input = Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.qcb.setImageBitmap(contactPhoto);
			
			
	
		}
		String currentStr = getAlpha(cb.getSortKey());
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";
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
