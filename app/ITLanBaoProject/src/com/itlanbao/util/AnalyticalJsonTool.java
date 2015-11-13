package com.itlanbao.util;

import com.google.gson.Gson;
import com.itlanbao.model.FriendListInfo;

/**
 * @author wjl
 * IT蓝豹 www.itlanbao.com
 * 解析json工具类
 */
public class AnalyticalJsonTool {

	private static final Gson gson = new Gson();
	
	/**
	 * 解析例子
	 * @param response
	 * @return
	 */
	public static FriendListInfo analyticalFriendList(String json){
        return gson.fromJson(json, FriendListInfo.class); 
	}
	
}
