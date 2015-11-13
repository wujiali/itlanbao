package com.itlanbao.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.itlanbao.app.R;
import com.itlanbao.app.fragment.ProjectFragment;
import com.itlanbao.app.fragment.InformationFragment;
import com.itlanbao.app.fragment.FriendsCircleFragment;
import com.itlanbao.app.fragment.SearchFragment;
import com.itlanbao.app.libs.DragLayout;
import com.itlanbao.app.libs.MyRelativeLayout;
import com.itlanbao.app.tabhost.TabFragment;
import com.itlanbao.applib.util.FileUtils;
import com.itlanbao.model.TabMode;

/**
 * IT蓝豹图片处理项目 平台来源 www.itlanbao.com
 * 
 * @author 吴家丽
 */
public class MainActivity extends ITLanBaoBaseActivity {

	public static final int HOME_TAB = 1000;
	public static final int FOUND_TAB = 2000;
	public static final int LIKE_TAB = 3000;
	public static final int MY_SPACE_TAB = 4000;

	private TabFragment actionBarFragment;
	private Context context;
	/**
	 * 左边侧滑菜单
	 */
	private DragLayout mDragLayout;
	private LinearLayout menu_header;
	private TextView menu_setting;

	private ListView menuListView;// 菜单列表

	private MyRelativeLayout myRelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 记住调用上传头像保存图片的时候记得先创建文件夹
		context = this;
		FileUtils.initCacheFile(this);
		MangerActivitys.addActivity(context);
		initView();// 初始化控件

		// 左侧页面listView添加数据
		List<Map<String, Object>> data = getMenuData();

		menuListView.setAdapter(new SimpleAdapter(this, data,
				R.layout.view_left_item,
				new String[] { "item", "image", "iv" }, new int[] {
						R.id.tv_item, R.id.iv_item, R.id.iv }));

		// TextView upload =(TextView)findViewById(R.id.upload);
		// upload.setOnClickListener(new TextView.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// //上传头像方法，showNewInsertPictureDialog
		// //该方法封装好了图片头像裁剪功能直接返回图片裁剪后图片路径和图片
		// showNewInsertPictureDialog(new InsertionPictureOnFinishListener() {
		//
		// @Override
		// public void onAddImageFinish(String imagePath, Bitmap bitmap) {
		//
		// image.setImageBitmap(bitmap);
		//
		// }
		//
		// });
		// }
		// });

	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		// MangerActivitys.addActivity(context);

		// 点击back按钮
		actionBarFragment = (TabFragment) getSupportFragmentManager()
				.findFragmentById(R.id.tab_bar_fragment);

		int code = 1;
		final ArrayList<TabMode> listTabModes = new ArrayList<TabMode>();
		{// 资讯部分 InformationFragment
			final TabMode tabMode = new TabMode(HOME_TAB,
					R.drawable.tab_1_selector,
					getString(R.string.str_tab_info),
					R.color.tab_text_color_selector, new InformationFragment(),
					code == 1);
			listTabModes.add(tabMode);
		}

		{// 发现页面
			final TabMode tabMode = new TabMode(FOUND_TAB,
					R.drawable.tab_2_selector,
					getString(R.string.str_tab_project),
					R.color.tab_text_color_selector, new ProjectFragment(),
					code == 2);//
			listTabModes.add(tabMode);

		}

		{// 蓝豹圈页面Circle of friends
			final TabMode tabMode = new TabMode(LIKE_TAB,
					R.drawable.tab_3_selector,
					getString(R.string.str_tab_lanbao_cirle),
					R.color.tab_text_color_selector,
					new FriendsCircleFragment(), code == 3);
			listTabModes.add(tabMode);
		}

		{// 搜索页面
			final TabMode tabMode = new TabMode(MY_SPACE_TAB,
					R.drawable.tab_4_selector,
					getString(R.string.str_tab_search),
					R.color.tab_text_color_selector, new SearchFragment(),
					code == 4);
			listTabModes.add(tabMode);
		}

		actionBarFragment.creatTab(MainActivity.this, listTabModes,
				new TabFragment.IFocusChangeListener() {

					@Override
					public void OnFocusChange(int currentTabId, int tabIndex) {

					}
				});

		// 这部分是底部menu的view控件
		menu_setting = (TextView) this.findViewById(R.id.iv_setting);
		menu_header = (LinearLayout) this.findViewById(R.id.menu_header);
		mDragLayout = (DragLayout) findViewById(R.id.dl);

		myRelativeLayout = (MyRelativeLayout) findViewById(R.id.rl_layout);
		mDragLayout.setBorder(actionBarFragment);
		myRelativeLayout.setDragLayout(mDragLayout);

		/**
		 * 抽屜动作监听
		 */
		mDragLayout
				.setOnLayoutDragingListener(new DragLayout.OnLayoutDragingListener() {

					@Override
					public void onOpen() {
						// 打开
					}

					@Override
					public void onDraging(float percent) {
						// 滑动中
					}

					@Override
					public void onClose() {
						// 关闭
					}
				});

		menuListView = (ListView) findViewById(R.id.menu_listview);// 抽屉列表
		initResideListener();// 个人中心、设置点击事件

	}

	/**
	 * 个人中心、设置点击事件
	 */
	private void initResideListener() {
		// 点击个人中心
		menu_header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		// 进入设置界面
		menu_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(context, SettingsActivity.class);
				// startActivity(intent);
			}
		});

	}

	/**
	 * 加载抽屉列表数据
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getMenuData() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;

		item = new HashMap<String, Object>();
		item.put("item", "关于IT蓝豹");
		item.put("image", R.drawable.ic_launcher);
		item.put("iv", R.drawable.icon_kehu_arrow);
		data.add(item);

		item = new HashMap<String, Object>();
		item.put("item", "一键预览");
		item.put("image", R.drawable.ic_launcher);
		item.put("iv", R.drawable.icon_kehu_arrow);
		data.add(item);

		item = new HashMap<String, Object>();
		item.put("item", "版本更新");
		item.put("image", R.drawable.ic_launcher);
		item.put("iv", R.drawable.icon_kehu_arrow);
		data.add(item);

		return data;
	}

	/**
	 * activity对象回收
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < MangerActivitys.activitys.size(); i++) {
			if (MangerActivitys.activitys.get(i) != null) {
				((Activity) MangerActivitys.activitys.get(i)).finish();
			}
		}
		finish();
		System.gc();
	}

	/**
	 * 返回键监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确定要退出吗？")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Intent.ACTION_MAIN);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.addCategory(Intent.CATEGORY_HOME);
									startActivity(intent);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
