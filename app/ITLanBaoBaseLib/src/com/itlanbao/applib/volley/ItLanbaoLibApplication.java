package com.itlanbao.applib.volley;
 
 
 

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ItLanbaoLibApplication extends Application{
	
	private static ItLanbaoLibApplication instance;
	 

	@Override
	public void onCreate() {
		super.onCreate(); 
		setInstance(this);
		initImageLoader(getApplicationContext());
	}
	 // 初始化ImageLoader
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).discCacheSize(10 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

	
	public static void setInstance(ItLanbaoLibApplication instance) {
		ItLanbaoLibApplication.instance = instance;
	}
	/**
	 * 获取ItLanBaoApplication实例
	 * 
	 * @return
	 */
	public static ItLanbaoLibApplication getInstance()
	{
		return instance;
	}
 
}
