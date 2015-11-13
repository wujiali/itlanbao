package com.itlanbao.applib;
 
 
 

import com.itlanbao.applib.volley.RequestApiData;

import android.app.Application;
import android.content.Context;
 

public class ItLanbaoLibApplication extends Application{
	
	private static ItLanbaoLibApplication instance;
	 
	private RequestApiData requestApi;
	
	@Override
	public void onCreate() {
		super.onCreate(); 
		setInstance(this);  
        requestApi = RequestApiData.getInstance();  
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
