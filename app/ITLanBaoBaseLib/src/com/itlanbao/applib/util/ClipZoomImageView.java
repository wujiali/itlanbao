package com.itlanbao.applib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * 图片裁剪
 * 
 * @author wjl
 * 
 */ 
public class ClipZoomImageView extends ImageView implements OnTouchListener,ViewTreeObserver.OnGlobalLayoutListener {

	/**
	 * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
	 */
	private boolean once = true;
	/** 记录是拖拉照片模式还是放大缩小照片模式 */
	private int mode = 0;// 初始状态
	/** 拖拉照片模式 */
	private static final int MODE_DRAG = 1;
	/** 放大缩小照片模式 */
	private static final int MODE_ZOOM = 2;

	/** 用于记录开始时候的坐标位置 */
	private PointF startPoint = new PointF();
	/** 用于记录拖拉图片移动的坐标位置 */
	private Matrix matrix = new Matrix();
	/** 用于记录图片要进行拖拉时候的坐标位置 */
	private Matrix currentMatrix = new Matrix();

	/** 两个手指的开始距离 */
	private float startDis;
	/** 两个手指的中间点 */
	private PointF midPoint;

	private float midPointX ;
	private float midPointY ;
	
	private ClipImageBorderView borderView ;
	
	public void setBorderView(ClipImageBorderView borderView) {
		this.borderView = borderView;
	}

	public ClipZoomImageView(Context context) {
		this(context, null);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setScaleType(ScaleType.MATRIX);
		this.setOnTouchListener(this);
	}

	/**
	 * 根据当前图片的Matrix获得图片的范围
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = this.matrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 手指压下屏幕
		case MotionEvent.ACTION_DOWN:
			mode = MODE_DRAG;
			// 记录ImageView当前的移动位置
			currentMatrix.set(ClipZoomImageView.this.getImageMatrix());
			startPoint.set(event.getX(), event.getY());
			break;
		// 手指在屏幕上移动，改事件会被不断触发
		case MotionEvent.ACTION_MOVE:
			// 拖拉图片
			if (mode == MODE_DRAG) {
				float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
				float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
				// 在没有移动之前的位置上进行移动
				matrix.set(currentMatrix);
				matrix.postTranslate(dx, dy);
			}
			// 放大缩小图片
			else if (mode == MODE_ZOOM) {
				float endDis = distance(event);// 结束距离
				if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					float scale = endDis / startDis;// 得到缩放倍数
					matrix.set(currentMatrix);
					midPointX = midPoint.x;
					midPointY = midPoint.y;
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					
				}
			}
			
			break;
		// 手指离开屏幕
		case MotionEvent.ACTION_UP:
			
			if(borderView == null){
				return false;
			}
			
			RectF rectF = borderView.getRectF();

			RectF rectF2 = getMatrixRectF();
			// 拿到图片的宽和高
			int dw = (int) (rectF2.right - rectF2.left);
			int dh = (int) (rectF2.bottom - rectF2.top);
			
			float scaleW = 1.0f;
			float scaleH = 1.0f;
			float scale = 1.0f;
			
			// 放大比例
			if (dw < rectF.right  - rectF.left) {
				scaleW =  (rectF.right  - rectF.left) / dw;
			}
			
			if(dh < rectF.bottom - rectF.top){
				scaleH = (rectF.bottom - rectF.top) / dh ;
			}
			
			scale = Math.max(scaleW, scaleH);
			if (dw < rectF.right  - rectF.left || dh < rectF.bottom - rectF.top) {
				matrix.postScale(scale, scale, midPointX,midPointY);
			}
			
			checkBorder();
			// 当触点离开屏幕，但是屏幕上还有触点(手指)
		case MotionEvent.ACTION_POINTER_UP:
			mode = 0;
			break;
		// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = MODE_ZOOM;
			// 计算两个手指间的距离
			startDis = distance(event);
			// 计算两个手指间的中间点
			if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
				midPoint = mid(event);
				// 记录当前ImageView的缩放倍数
				currentMatrix.set(ClipZoomImageView.this.getImageMatrix());
			}
			break;
		}
		ClipZoomImageView.this.setImageMatrix(matrix);
		return true;

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 水平方向与View的边距
	 */
	private int clipImageWid;
	/**
	 * 垂直方向与View的边距
	 */
	private int clipImageHgt;

	@Override
	public void onGlobalLayout() {
		if (once) {
			Drawable d = getDrawable();
			if (d == null)
				return;
			int width = getWidth();
			int height = getHeight();
			// 拿到图片的宽和高
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			float scale = 1.0f;
			if (dw < getWidth() - (getWidth() / 2 - clipImageWid / 2) * 2
					&& dh > getHeight() - (getHeight() / 2 - clipImageHgt / 2)
							* 2) {
				scale = (getWidth() * 1.0f - (getWidth() / 2 - clipImageWid / 2) * 2)
						/ dw;
			}

			if (dh < getHeight() - (getHeight() / 2 - clipImageHgt / 2) * 2
					&& dw > getWidth() - (getWidth() / 2 - clipImageWid / 2)
							* 2) {
				scale = (getHeight() * 1.0f - (getHeight() / 2 - clipImageHgt / 2) * 2)
						/ dh;
			}

			if (dw < getWidth() - (getWidth() / 2 - clipImageWid / 2) * 2
					&& dh < getHeight() - (getHeight() / 2 - clipImageHgt / 2)
							* 2) {
				float scaleW = (getWidth() * 1.0f - (getWidth() / 2 - clipImageWid / 2) * 2)
						/ dw;
				float scaleH = (getHeight() * 1.0f - (getHeight() / 2 - clipImageHgt / 2) * 2)
						/ dh;
				scale = Math.max(scaleW, scaleH);
			}

			matrix.postTranslate((width - dw) / 2, (height - dh) / 2);
			matrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
			// 图片移动至屏幕中心
			setImageMatrix(matrix);
			once = false;
		}

	}

	/**
	 * 剪切图片，返回剪切后的bitmap对象
	 * 
	 * @return
	 */
	public Bitmap clip() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		// 高度------修改
		return Bitmap.createBitmap(bitmap, (getWidth() / 2 - clipImageWid / 2),
				(getHeight() / 2 - clipImageHgt / 2), getWidth() - 2
						* (getWidth() / 2 - clipImageWid / 2), getHeight() - 2
						* (getHeight() / 2 - clipImageHgt / 2));
	}

	/**
	 * 边界检测
	 */
	private void checkBorder() {

		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		// 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
//		if (rect.width() + 0.01 >= width - 2 * (getWidth() / 2 - clipImageWid / 2)) {
			if (rect.left > (getWidth() / 2 - clipImageWid / 2)) {
				deltaX = -rect.left + (getWidth() / 2 - clipImageWid / 2);
			}
			if (rect.right < width - (getWidth() / 2 - clipImageWid / 2)) {
				deltaX = width - (getWidth() / 2 - clipImageWid / 2)- rect.right;
			}
//		}
		
//		if (rect.height() + 0.01 >= height - 2 * (getHeight() / 2 - clipImageHgt / 2)) {
			if (rect.top > (getHeight() / 2 - clipImageHgt / 2)) {
				deltaY = -rect.top + (getHeight() / 2 - clipImageHgt / 2);
			}
			if (rect.bottom < height - (getHeight() / 2 - clipImageHgt / 2)) {
				deltaY = height - (getHeight() / 2 - clipImageHgt / 2)
						- rect.bottom;
			}
//		}
		
		matrix.postTranslate(deltaX, deltaY);

	}

	public void setClipImageWid(int clipImageWid) {
		this.clipImageWid = clipImageWid;
	}

	public void setClipImageHgt(int clipImageHgt) {
		this.clipImageHgt = clipImageHgt;
	}

	/** 计算两个手指间的距离 */
	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** 使用勾股定理返回两点之间的距离 */
	 
		return FloatMath.sqrt( dx * dx + dy * dy);
	}

	/** 计算两个手指间的中间点 */
	private PointF mid(MotionEvent event) {
		float midX = (event.getX(1) + event.getX(0)) / 2;
		float midY = (event.getY(1) + event.getY(0)) / 2;
		return new PointF(midX, midY);
	}
}
