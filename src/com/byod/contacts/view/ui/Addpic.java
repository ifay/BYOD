package com.byod.contacts.view.ui;



import com.byod.R;
import com.byod.contacts.view.HomeContactActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

public class Addpic extends Activity implements ViewFactory {

	
	ImageButton imageButton;//头像按钮
	View imageChooseView;//图像选择的视图
	AlertDialog imageChooseDialog;//头像选择对话框
	Gallery gallery;//头像的Gallery
	ImageSwitcher is;//头像的ImageSwitcher
	int currentImagePosition;//用于记录当前选中图像在图像数组中的位置
	int previousImagePosition;//用于记录上一次图片的位置
	public static boolean imageChanged;//判断头像有没有变化
	public static int tt;
	public static int pp;
	/**
	 * 所有的图像图片
	 */
	private  int[] images 
			= new int[]{R.drawable.andriod0001
		,R.drawable.andriod0001,R.drawable.andriod0002,R.drawable.andriod0003
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew);
		Intent intent = getIntent();
		imageButton = (ImageButton)findViewById(R.id.qcb);
		loadImage();//为gallery装载图片
		initImageChooseDialog();//初始化imageChooseDialog
		imageChooseDialog.show();
		
	}
	public void loadImage() {
		if(imageChooseView == null) {
			
			LayoutInflater li = LayoutInflater.from(Addpic.this);
			imageChooseView = li.inflate(R.layout.imageswitch, null);
			
			//通过渲染xml文件，得到一个视图（View），再拿到这个View里面的Gallery
			gallery = (Gallery)imageChooseView.findViewById(R.id.gallery);
			//为Gallery装载图片
			gallery.setAdapter(new ImageAdapter(this));
			gallery.setSelection(images.length/2);
			is = (ImageSwitcher)imageChooseView.findViewById(R.id.imageswitch);
			is.setFactory(this);
			is.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
	        //卸载图片的动画效果
	        is.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
			gallery.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					//当前的头像位置为选中的位置
					currentImagePosition = arg2;
					//为ImageSwitcher设置图像
					is.setImageResource(images[arg2 % images.length]);
					
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}});
		}
		
	}
	/**
	 * 自定义Gallery的适配器
	 * @author Administrator
	 *
	 */
	class ImageAdapter extends BaseAdapter {

		private Context context;
		
		public ImageAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		/**
		 * gallery从这个方法中拿到image
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(images[position%images.length]);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new Gallery.LayoutParams(80,80));
			iv.setPadding(15, 10, 15, 10);
			return iv;
			
		}
		
	}
	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setBackgroundColor(0xff000000);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new ImageSwitcher.LayoutParams(90,90));
		return view;
	}
	public void initImageChooseDialog() {
		if(imageChooseDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择图像")
			.setView(imageChooseView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					imageChanged = true;
					previousImagePosition = currentImagePosition;
					tt=currentImagePosition%images.length;
					pp=HomeContactActivity.positioncommon;
					//imageButton.setImageResource(images[currentImagePosition%images.length]);
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					currentImagePosition = previousImagePosition;
					
				}
			});
			imageChooseDialog = builder.create();
		}
	}
	/**
	 * 当退出的时候，回收资源
	 */
	@Override
	protected void onDestroy() {
		if(is != null) {
			is = null;
		}
		if(gallery != null) {
			gallery = null;
		}
		if(imageChooseDialog != null) {
			imageChooseDialog = null;
		}
		if(imageChooseView != null) {
			imageChooseView = null;
		}
		if(imageButton != null) {
			imageButton = null;
		}
		
		super.onDestroy();
	}
}
