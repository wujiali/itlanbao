package com.itlanbao.applib.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author lichao
 * 头像裁切裁框
 */
public class ClipImageBorderView extends View
{
	/**
	 * 水平方向与View的边距
	 */
	private int clipImageWid;
	/**
	 * 垂直方向与View的边距
	 */
	private int clipImageHgt;
	
	/**
	 * 边框的颜色，默认为白色
	 */
	private int mBorderColor = Color.parseColor("#FFFFFF");
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 1;

	private Paint mPaint;

	private RectF rectF = new RectF();
	public RectF getRectF() {
		return rectF;
	}

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		mPaint.setColor(Color.parseColor("#aa000000"));
		mPaint.setStyle(Style.FILL);
		
		// 绘制左边1
		canvas.drawRect(0, 0, getWidth() / 2 - clipImageWid / 2, getHeight(), mPaint);
		// 绘制右边2
		canvas.drawRect(getWidth() / 2 + clipImageWid / 2, 0, getWidth(),getHeight(), mPaint);
		
		// 绘制上边3
		canvas.drawRect(getWidth() / 2 - clipImageWid / 2, 0, getWidth() / 2 + clipImageWid / 2,getHeight() / 2 - clipImageHgt / 2, mPaint);
		// 绘制下边4
		canvas.drawRect(getWidth() / 2 - clipImageWid / 2, getHeight() / 2 + clipImageHgt / 2,getWidth() / 2 + clipImageWid / 2, getHeight(), mPaint);
		
		mPaint.setColor(mBorderColor);
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		// 绘制外边框
		canvas.drawRect(getWidth() / 2 - clipImageWid / 2, getHeight() / 2 - clipImageHgt / 2, getWidth() / 2 + clipImageWid / 2, getHeight() / 2 + clipImageHgt / 2, mPaint);
		// 保存此框边信息
		int left = getWidth() / 2 - clipImageWid / 2;
		int top = getHeight() / 2 - clipImageHgt / 2;
		int right = getWidth() / 2 + clipImageWid / 2;
		int bottom = getHeight() / 2 + clipImageHgt / 2;
		Rect src = new Rect(left, top,right , bottom);
		rectF.set(src);
		super.onDraw(canvas);

	}

	public void setClipImageWid(int clipImageWid)
	{
		this.clipImageWid = clipImageWid;
	}
	
	public void setClipImageHgt(int clipImageHgt)
	{
		this.clipImageHgt = clipImageHgt;
	}
	
}
