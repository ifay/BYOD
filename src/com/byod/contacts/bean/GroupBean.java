package com.byod.contacts.bean;

import java.util.ArrayList;

public class GroupBean {

	private int id;
	private String name;
	private ArrayList<ContactBean> contact;
	private int count;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<ContactBean> getContact() {
		return contact;
	}
	public void setContact(ArrayList<ContactBean> contact) {
		this.contact = contact;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
