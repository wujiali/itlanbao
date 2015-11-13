package com.itlanbao.app.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.itlanbao.listener.InsertPictureDialog;
import com.itlanbao.listener.InsertPictureDialog.InsertionPictureOnFinishListener;

/**
 * @author wjl
 * ITLanBaoBaseActivity 基类，共同功能可以再基类处理
 */
public abstract class ITLanBaoBaseActivity extends FragmentActivity {

	private boolean isExit;

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 显示插入图片提示框
	 * 
	 * @param listener
	 */
	public void showNewInsertPictureDialog(
			InsertionPictureOnFinishListener listener) {
		// 默认修改头像，允许裁切
		showInsertPictureDialog(5, true, listener);
	}

	public void showInsertPictureDialog(int uploadType, boolean isCrop,
			InsertionPictureOnFinishListener listener) {
		final FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		final Fragment prev = getSupportFragmentManager().findFragmentByTag(
				"dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		final InsertPictureDialog insertPictureDialog = InsertPictureDialog
				.newInstance(uploadType, isCrop);
		insertPictureDialog.setOnFinishListener(listener);
		insertPictureDialog.show(getSupportFragmentManager(), "dialog");
	}

}
