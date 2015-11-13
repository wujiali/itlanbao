package com.itlanbao.model;

import java.util.ArrayList;

/**
 * @author wjl
 * 好友推荐列表
 */
public class FriendListInfo {

	
	private int friendListId; //好友列表id
	private int code;//好友列表返回状态码
	private String errorMsg;//返回的错误信息
	private ArrayList<FriendDynamic> friendList;//好友列表
	
	
	public int getFriendListId() {
		return friendListId;
	}

	public void setFriendListId(int friendListId) {
		this.friendListId = friendListId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public ArrayList<FriendDynamic> getFriendList() {
		return friendList;
	}

	public void setFriendList(ArrayList<FriendDynamic> friendList) {
		this.friendList = friendList;
	}

 
}
