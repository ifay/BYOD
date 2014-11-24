package com.byod.contacts.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.byod.R;

public class HomeSettintActivity extends Activity {
	
	
	private Button addContactBtn;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_setting_page);
		addContactBtn =(Button) findViewById(R.id.addContactBtn);
		addContactBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Toast.makeText(getApplicationContext(), "联系人去重成功",
					     Toast.LENGTH_SHORT).show();
			 
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
}
