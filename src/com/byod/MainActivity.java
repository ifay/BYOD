package com.byod;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byod.application.UserRegisterPage1;
import com.byod.application.appmanager.AppManager;
import com.byod.application.watcher.AppWatcherService;
import com.byod.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * @author ifay
 *         <p/>
 *         主要测试页面，显示所有不安全权限应用，显示设备ID...
 */
public class MainActivity extends BYODActivity implements OnItemClickListener {

    // views
    private TextView tv = null;
    private ListView lv = null;
    private Button register = null;

    private MyAdapter mAdapter;
    private ArrayList<PackageInfo> mMaliciousAppList;

    private Handler mHandler;
    private AppWatcherService.MyBinder mBinder;

    // test------
    private ActivityManager mActivityManager;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (AppWatcherService.MyBinder) service;
            mBinder.getRunningServices();
        }
    };

    @Override
    public void onCreate() {
        //get malicious permission apps
        //TODO 用Handler处理，不然加载速度太慢
        mMaliciousAppList = AppManager.getInstance().getSensitiveApplications(false);
        initView();

        // 获得设备ID
        tv.setText("IMEI:" + DeviceUtils.getInstance(this).getIMEI() + "\n" +
                "IMSI:" + DeviceUtils.getInstance(this).getIMSI() + "\n" +
                "TEL:" + DeviceUtils.getInstance(this).getTEL());

        //开启监控---should start after login
        Intent service = new Intent(this, AppWatcherService.class);
        startService(service);
//         bindService(service, serviceConn, BIND_ABOVE_CLIENT);
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
        //注册
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this, UserRegisterPage1.class);
                startActivity(regIntent);
            }
        });
        mAdapter = new MyAdapter(this);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
    }

    // test-------获得最近使用的应用，
    // 将来可以做成一个服务，持续监控topActivity
    // TODO 监控service
    private void testSth() {
//         mActivityManager = (ActivityManager)this.getSystemService("activity");
//         ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
//         Log.d("test",topActivity.getPackageName());
//         tv.setText(topActivity.getPackageName());
    }

    //弹窗显式的是权限值，而非名称 TODO 隐藏的方法AppSecurity***，或者自己定义权限名称
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //data src
        String[] perms = mMaliciousAppList.get(position).requestedPermissions;

        LinearLayout permView = new LinearLayout(this);
        permView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        permView.setBackgroundColor(Color.WHITE);
        ListView permListView = new ListView(this);
        permListView.setBackgroundColor(Color.WHITE);
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
            icon = (ImageView) rootView.findViewById(R.id.icon);
            pkgName = (TextView) rootView.findViewById(R.id.name);
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
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            setItemContent(holder, position);
            return convertView;
        }

        private void setItemContent(ViewHolder holder, int position) {
            PackageInfo pi = mMaliciousAppList.get(position);
            ApplicationInfo ai = pi.applicationInfo;
            Drawable icon = ai.loadIcon(mContext.getPackageManager());
            holder.icon.setImageDrawable(icon);
            //get app name
            holder.pkgName.setText(pi.applicationInfo.loadLabel(MainActivity.this.getPackageManager()));
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "onclick", Toast.LENGTH_SHORT).show();
            //TODO useless?
        }

    }

    @Override
    protected void onDestroy() {
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
        super.onResume();
        Log.d("test", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("test", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("test", "onStop");
    }
}
