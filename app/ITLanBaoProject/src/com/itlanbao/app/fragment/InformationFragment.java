package com.itlanbao.app.fragment;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itlanbao.app.R;
import com.itlanbao.app.adapter.FriendAdapter;
import com.itlanbao.applib.pullfreshview.PullRefreshListView;
import com.itlanbao.applib.util.FileUtils;
import com.itlanbao.applib.util.date.DateUtils;
import com.itlanbao.model.FriendDynamic;
import com.itlanbao.model.FriendListInfo;
import com.itlanbao.util.AnalyticalJsonTool;

/**
 * @author wjl
 * www.itlanbao.com
 * Information 资讯部分
 */
public class InformationFragment extends MyFragment implements PullRefreshListView.IXListViewListener{

	private View view;
	private PullRefreshListView mListView;
	private FriendAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }


	@Override
	protected void onVisible(boolean isInit) {
		// TODO Auto-generated method stub
		if(isInit){
			mListView = (PullRefreshListView) view.findViewById(R.id.pull_listview);

			mListView.setPullRefreshEnable(true);
			mListView.setPullLoadEnable(false);
			mListView.setXListViewListener(this);
			mListView.setRefreshTime(DateUtils.getNowDateFormat(DateUtils.DATE_FORMAT_6));
			//加载数据
			loadFriendListDate();
		}
	}

	/**
	 * 加载好友动态数据
	 */
	private void loadFriendListDate(){
		new LoadAsynTask().execute();
	}

	class LoadAsynTask extends AsyncTask<Void, Void, FriendListInfo>{

		@Override
		protected FriendListInfo doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//通过获取assets里面数据，解析assets的方法FileUtils.getFromAssets
			String friendInfo = FileUtils.getFromAssets(getActivity(), "friendinfo.txt");
			//解析工具类直接返回对象
			return AnalyticalJsonTool.analyticalFriendList(friendInfo);
		}

		@Override
		protected void onPostExecute(FriendListInfo result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//返回数据刷新页面方法
			if(result!=null){
				ArrayList<FriendDynamic> mList = result.getFriendList();
				if(mList!=null&&mList.size()>0){
					if(mAdapter ==null){
						mAdapter = new FriendAdapter(getActivity(), mList);
						mListView.setAdapter(mAdapter);
					}else{
						mAdapter.setData(mList);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//准备异步时处理一些加载框
		}



	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}



}
