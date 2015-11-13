package com.itlanbao.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.itlanbao.applib.ItLanbaoLibApplication;
import com.itlanbao.applib.volley.RequestApiData;
import com.itlanbao.applib.volley.RequestManager;

/**
 * @author wjl IT蓝豹 ItLanBaoApplication主要作用是处理一些app全局变量，
 */
public class ItLanBaoApplication extends ItLanbaoLibApplication {

	private static ItLanBaoApplication instance;
	private RequestApiData requestApi;

	private int screenWidth = -1;
	private int screenHeight = -1;
	private int densityDpi = -1;
	private float density = -1;

	// 渠道号
	private String fid = "";
	// 版本号
	private String version = "";

	@Override
	public void onCreate() {
		super.onCreate();
		setInstance(this);
		init();
		requestApi = RequestApiData.getInstance();
	}

	private void init() {
		RequestManager.init(getApplicationContext());
	}

	public static void setInstance(ItLanBaoApplication instance) {
		ItLanBaoApplication.instance = instance;
	}

	/**
	 * 获取渠道号
	 * 
	 * @return
	 */
	public String getFid() {

		try {
			if (TextUtils.isEmpty(fid)) {
				final ApplicationInfo ai = getPackageManager()
						.getApplicationInfo(getPackageName(),
								PackageManager.GET_META_DATA);
				fid = ai.metaData.get("fid").toString();
				if (fid == null) {
					fid = "";
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return fid;
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		try {
			if (TextUtils.isEmpty(version)) {
				final ApplicationInfo ai = getPackageManager()
						.getApplicationInfo(getPackageName(),
								PackageManager.GET_META_DATA);
				version = ai.metaData.get("version").toString();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取ItLanBaoApplication实例
	 * 
	 * @return
	 */
	public static ItLanBaoApplication getInstance() {
		return instance;
	}

	public String getVersionName() {
		String version = "";
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
 
	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getDensityDpi() {
		return densityDpi;
	}

	public void setDensityDpi(int densityDpi) {
		this.densityDpi = densityDpi;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

}
