package com.itlanbao.applib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 图片裁剪
 * 
 * @author lichao
 *
 */
public class ClipImageLayout extends RelativeLayout {

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);

	}

	public void init(int clipImageWid, int clipImageHgt, String uri) {
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		mZoomImageView.setImageBitmap(ImageUtil.getSmallBitmap(uri));
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		mZoomImageView.setClipImageWid(clipImageWid);
		mClipImageView.setClipImageWid(clipImageWid);
		mZoomImageView.setClipImageHgt(clipImageHgt);
		mClipImageView.setClipImageHgt(clipImageHgt);
		
		mZoomImageView.setBorderView(mClipImageView);
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mZoomImageView.clip();
	}

}
