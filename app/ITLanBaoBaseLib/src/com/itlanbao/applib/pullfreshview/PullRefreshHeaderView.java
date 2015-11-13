package com.itlanbao.applib.pullfreshview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itlanbao.applib.R;

/**
 * @author wjl
 * @file XHeaderView.java
 * @create Apr 18, 2012 5:22:27 PM
 * @description XListView's header
 */
public class PullRefreshHeaderView extends LinearLayout {
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    private boolean mIsFirst;
    private boolean isSetHintText = false;

    public PullRefreshHeaderView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public PullRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.pull_refresh_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.header_arrow);
        mHintTextView = (TextView) findViewById(R.id.header_hint_text);
        mProgressBar = (ProgressBar) findViewById(R.id.header_progressbar);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState && mIsFirst) {
            mIsFirst = true;
            return;
        }

        if (state == STATE_REFRESHING) { // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else { // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mArrowImageView.getVisibility() == View.VISIBLE) {
                    if (mState == STATE_READY) {
                        mArrowImageView.startAnimation(mRotateDownAnim);
                    }
                    if (mState == STATE_REFRESHING) {
                        mArrowImageView.clearAnimation();
                    }
                }
                if (!isSetHintText) {
                    mHintTextView.setText(R.string.header_hint_refresh_normal);
                }
                break;

            case STATE_READY:
                if (mState != STATE_READY) {
                    if (mArrowImageView.getVisibility() == View.VISIBLE) {
                        mArrowImageView.clearAnimation();
                        mArrowImageView.startAnimation(mRotateUpAnim);
                    }
                    if (!isSetHintText) {
                        mHintTextView.setText(R.string.header_hint_refresh_ready);
                    }
                }
                break;

            case STATE_REFRESHING:
                if (!isSetHintText) {
                    mHintTextView.setText(R.string.header_hint_refresh_loading);
                }
                break;
            default:
        }

        mState = state;
    }
    
    /**
     * 设置下拉加载提示文字
     * 
     * @param hintText
     */
    public void setPullRefreshHintText(String hintText) {
        if (!TextUtils.isEmpty(hintText)) {
            isSetHintText = true;
            mHintTextView.setText(hintText);
        }
    }

    public void setVisiableHeight(int height) {
        if (height < 0) height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

}
