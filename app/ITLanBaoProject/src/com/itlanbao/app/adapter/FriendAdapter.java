package com.itlanbao.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.itlanbao.app.R;
import com.itlanbao.model.FriendDynamic;

/**
 * @author wjl
 * IT蓝豹
 * 好友模块
 */
public class FriendAdapter extends BaseAdapter{
	private ArrayList<FriendDynamic> mList ;
	private final int ITEMCOUNT = 4;// 消息类型的总数
	private Context mContext;
	private LayoutInflater mInflater;
	public FriendAdapter(Context mContext,ArrayList<FriendDynamic> mList){
		this.mList = mList;
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
	}
	
	public void setData(ArrayList<FriendDynamic> mList){
		if(mList ==null){
			mList =new ArrayList<FriendDynamic>();
		}
		mList.addAll(mList); 
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList!=null){
			return	mList.size();
		}
		return 0;
	}

	@Override
	public FriendDynamic getItem(int position) {
		// TODO Auto-generated method stub
		if(mList!=null){
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return ITEMCOUNT;
	}
	 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub 
		 
		 if(mList!=null){
			 FriendDynamic mInfo = mList.get(position);
			 int type = mInfo.getDisplayType(); 
			 ViewHolder viewHolder = null;
			 if(convertView == null){
				 viewHolder = new ViewHolder();
//				 if(type == 0){
					 //类型1
					 convertView = mInflater.inflate(R.layout.item_friend_type1, null);
//				 }else if(type == 1){
//					 //类型2
//				 }
				 
			 }else{
				 
			 }
		 }
		
		 
		return convertView;
	}
	
	class ViewHolder{
		
	}

}
