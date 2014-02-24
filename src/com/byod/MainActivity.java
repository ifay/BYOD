
package com.byod;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.application.appmanager.AppManager;
import com.byod.device.DeviceUtils;

public class MainActivity extends Activity implements OnItemClickListener {

    // views
    private TextView tv = null;
    private ListView lv = null;

    private MyAdapter mAdapter;
    private ArrayList<PackageInfo> mMaliciousAppList;
    
    private Handler mHandler;

    // test------
    private ActivityManager mActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 用Handler处理，不然加载速度太慢
        mMaliciousAppList = AppManager.getInstance().getSensitiveApplications(false);
        initView();
        // 获得设备ID
         tv.setText(DeviceUtils.getInstance(this).getIMEI()+"\n"+
         DeviceUtils.getInstance(this).getIMSI()+"\n"+
         DeviceUtils.getInstance(this).getTEL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textview);
        lv = (ListView) findViewById(R.id.lv);
        mAdapter = new MyAdapter(this);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
    }

    // test-------获得最近使用的应用，
    // 将来可以做成一个服务，持续监控topActivity
    // TODO 监控service
    private void testSth() {
        // mActivityManager =
        // (ActivityManager)this.getSystemService("activity");
        // ComponentName topActivity =
        // mActivityManager.getRunningTasks(1).get(0).topActivity;
        // Log.d("test",topActivity.getPackageName());
        // tv.setText(topActivity.getPackageName());
    }
    
    //弹窗显式的是权限值，而非名称
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] perms = mMaliciousAppList.get(position).requestedPermissions;

        LinearLayout permView = new LinearLayout(this);
        permView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        ListView permListView = new ListView(this);
        permListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, perms));
        permView.addView(permListView);
        final AlertDialog dlg = new AlertDialog.Builder(this)
            .setTitle(R.string.all_perm).setView(permView)
            .setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create();
        dlg.show();
    }

    private static class ViewHolder {
        ImageView icon;
        TextView pkgName;
        public ViewHolder(View rootView) {
            icon = (ImageView)rootView.findViewById(R.id.icon);
            pkgName = (TextView)rootView.findViewById(R.id.name);
        }
    }
    private class MyAdapter extends BaseAdapter implements View.OnClickListener {
        private Context mContext;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMaliciousAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMaliciousAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            setItemContent(holder,position);
            return convertView;
        }

        private void setItemContent(ViewHolder holder, int position) {
            PackageInfo pi = mMaliciousAppList.get(position);
            ApplicationInfo ai = pi.applicationInfo;
            Drawable icon = ai.loadIcon(mContext.getPackageManager());
            holder.icon.setImageDrawable(icon);
            holder.pkgName.setText(pi.packageName);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Toast.makeText(mContext, "onclick", Toast.LENGTH_SHORT).show();
            //TODO useless?
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("test", "onDestroy");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d("test", "onDestroy");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("test", "onResume");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.d("test", "onStart");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d("test", "onStop");
    }
}
