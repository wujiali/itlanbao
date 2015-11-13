package com.itlanbao.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.itlanbao.app.R;

/**
 * @author wjl
 * IT蓝豹 itlanbao.com
 * 标题view
 */
public class TitleBarView extends LinearLayout{

	private Context context;
	
	public TitleBarView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	
	private void init(Context context){
		this.context = context;
		 final View view = LayoutInflater.from(context).inflate(R.layout.view_head_titleview, null, false);
	        addView(view);
	}

}
