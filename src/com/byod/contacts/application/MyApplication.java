package com.byod.contacts.application;

import android.content.Intent;
import com.byod.contacts.bean.ContactBean;
import com.byod.contacts.service.T9Service;

import java.util.List;

public class MyApplication extends android.app.Application {

	
	private List<ContactBean> contactBeanList;
	
	public List<ContactBean> getContactBeanList() {
		return contactBeanList;
	}
	public void setContactBeanList(List<ContactBean> contactBeanList) {
		this.contactBeanList = contactBeanList;
	}

	@Override
	public void onCreate() {
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
	}
}
