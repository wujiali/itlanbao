package com.itlanbao.applib.volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.itlanbao.applib.util.LogUtils;
import com.itlanbao.applib.util.UserPreference;

/*
 * 网络接口 wjl
 */
public class RequestApiData {
	private static RequestApiData instance = null;

	private static String token;

	public static RequestApiData getInstance() {
		if (instance == null) { 
			instance = new RequestApiData();
		}
		return instance;
	}

	protected void executeRequest(Request<?> request) {
		RequestManager.addRequest(request, this);
	}
//
//	/**
//	 * @param 消息提示
//	 * @param errorListener
//	 * @param userId
//	 * @param type
//	 */
//	public void getNoticeNewNumAll(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		String path = UrlParser("notice/newNumAll.do", map);
//		executeRequest(new JsonObjectRequest(Method.GET, path, null, responseListener, errorListener));
//	}
//
//	/*
//	 * 喜欢按钮点击状态变化 type =0,1, 表示是否有返回值0 不返回 1返回
//	 */
//	public void getAttentionInfo(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener, String userId, int type) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("userId", userId);
//		map.put("res", type + "");
//		String path = UrlParser("space/attention.do", map);
//		executeRequest(new JsonObjectRequest(Method.GET, path, null, responseListener, errorListener));
//	}
// 
//	/*
//	 * 所有表情页面
//	 */
//	public void getAllEmotionList(Listener<String> responseListener, Response.ErrorListener errorListener) {
//		String path = Constants.API_URL + "data/emotionList.do?";
//
//		executeRequest(new StringRequest(Method.POST, path, responseListener, errorListener) {
//			protected Map<String, String> getParams() {
//				return new ApiParams();
//			}
//		});
//	}
//
//	/**
//	 * 注册页面
//	 */
//
//	public void register(final HashMap<String, String> parameter, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
//		String path = Constants.API_URL + "user/register.do?";
//
//		executeRequest(new StringRequest(Method.POST, path, responseListener, errorListener) {
//			protected Map<String, String> getParams() {
//				ApiParams parter = new ApiParams();
//				for (Entry<String, String> entry : parameter.entrySet()) {
//					parter.with(entry.getKey(), entry.getValue());
//				}
//				return parter;// new ApiParams().with("username",
//								// name).with("password",
//								// pass).with("fromChannel",
//								// Constants.fromChannle);
//			}
//		});
//	}

                
	/*
	 * get参数
	 */
	private String UrlParser(String uri,String method, HashMap<String, String> parameter) {
		token = UserPreference.read("token", null);
		if (token != null) {
			parameter.put("token", token);
//			parameter.put("fromChannel", BirdsLoveAppcation.getInstance().getFid());
//			parameter.put("version", BirdsLoveAppcation.getInstance().getVersion());
		}

		StringBuilder sb = new StringBuilder(uri);
		sb.append(method);
		sb.append('?');
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : parameter.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		sb.append(URLEncodedUtils.format(list, "UTF-8"));

		  if (LogUtils.DEBUG) {
			  LogUtils.e(getClass().getName(), sb.toString());
		}
		return sb.toString();
	}

	/*
	 * post参数
	 */
	private ApiParams getPostApiParmes(final HashMap<String, String> parameter) {
		ApiParams api = new ApiParams();

		for (Entry<String, String> entry : parameter.entrySet()) {
		    if (LogUtils.DEBUG) {
		    	LogUtils.d(getClass().getName(), "key=" + entry.getKey() + ";;value=" + entry.getValue());
		    }
			api.with(entry.getKey(), entry.getValue());
		}
		token = UserPreference.read("token", null);
		if (!TextUtils.isEmpty(token)) {
			api.with("token", token);
		}
//		api.with("fromChannel", BirdsLoveAppcation.getInstance().getFid());
//		api.with("version", BirdsLoveAppcation.getInstance().getVersion());

		if (LogUtils.DEBUG) {
			LogUtils.e(getClass().getName(), "" + api);
		}
		return api;
	}

	    

}
