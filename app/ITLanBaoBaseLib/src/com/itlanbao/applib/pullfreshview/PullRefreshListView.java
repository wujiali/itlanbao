package com.itlanbao.applib.pullfreshview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.itlanbao.applib.R;
import com.itlanbao.applib.util.LogUtils;

/**
 * XListView
 *
 * 根据wjl的XListView改造而来，参考Github链接：https://github.com/Maxwin-z/XListView-Android
 *
 * @author wjl
 * @date 2013-10-08
 */
public class PullRefreshListView extends ListView implements OnScrollListener {
//    private static final String TAG = "XListView";

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    private PullRefreshHeaderView mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.
    // -- footer view
    private PullRefreshFooterView mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;
    // 是否自动触发上拉加载
    private boolean isAutoPullLoad = true;
    // FooterView是否支持上拉加载
    private boolean isPullLoadFooterView = true;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.

    private LinearLayout mFooterLayout;
    
    private int mScrollDistance;
    /**滑动的距离**/
    public int getmScrollDistance() {
		return mScrollDistance;
	}

    
    public PullRefreshListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        // init header view
        mHeaderView = new PullRefreshHeaderView(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.header_content);
        mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.header_hint_time);
        
        addHeaderView(mHeaderView); 
        
        // init footer view
        mFooterView = new PullRefreshFooterView(context);
        mFooterLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mFooterLayout.addView(mFooterView, params);

        // init header height
        ViewTreeObserver observer = mHeaderView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mHeaderViewHeight = mHeaderViewContent.getHeight();
                    ViewTreeObserver observer = getViewTreeObserver();
                    if (null != observer) {
                    	final int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                		if (version >= 16) {
                			observer.removeGlobalOnLayoutListener(this);
                		}
                    }
                }
            });
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XFooterView is the last footer view, and only add once.
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFooterLayout);
        }
        super.setAdapter(adapter);
    }

    /**
     * 设置下拉加载提示文字
     * 
     * @param hintText
     */
    public void setPullRefreshHintText(String hintText) {
        mHeaderView.setPullRefreshHintText(hintText);
    }
    
    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.setBottomMargin(0);
            mFooterView.hide();
            mFooterView.setPadding(0, 0, 0, mFooterView.getHeight() * (-1));
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.setPadding(0, 0, 0, 0);
            mFooterView.show();
            mFooterView.setState(PullRefreshFooterView.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }
    
    /**
     * 删除上拉加载更多按钮
     */
    public void removePullLoadFooter() {
        if (mFooterLayout != null) {
            removeFooterView(mFooterLayout);
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(PullRefreshFooterView.STATE_NORMAL);
        }
    }
    
    /**
     * 是否开启自动触发上拉加载更多，默认为true
     * @param isAutoPullLoad
     */
    public void setAutoPullLoad(boolean isAutoPullLoad) {
    	this.isAutoPullLoad = isAutoPullLoad;
    }
    

    /**
     * FooterView是否支持上拉加载
     * 
     * @param isPullLoadFooterView
     */
    public void setPullLoadFooterView(boolean isPullLoadFooterView) {
        this.isPullLoadFooterView = isPullLoadFooterView;
    }

    /**
     * 加载失败
     */
    public void loadMoreFailed() {
    	if (mPullLoading) {
            mPullLoading = false;
        	setPullRefreshEnable(false);
            mFooterView.setState(PullRefreshFooterView.STATE_FAILED);
        }
    }
    
    /**
     * 设置加载更多提示文字
     * 
     * @param text
     */
    public void setLoadMoreText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mFooterView.setLoadMoreText(text);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
    	if (!TextUtils.isEmpty(time)) {
    		 mHeaderTimeView.setText(time);
    		 ((ViewGroup)mHeaderTimeView.getParent()).setVisibility(View.GONE);
    	} else {
    		((ViewGroup)mHeaderTimeView.getParent()).setVisibility(View.GONE);
    	}
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(PullRefreshHeaderView.STATE_READY);
            } else {
                mHeaderView.setState(PullRefreshHeaderView.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * 直接关闭HeaderView
     */
    public void closeHeader() {
        resetHeaderHeight(mHeaderViewHeight);
    }
    
    /**
     * reset header view's height.
     */
    public void resetHeaderHeight() {
        resetHeaderHeight(-1);
    }
    public void resetHeaderHeight(int h) {
        int height = h == -1 ? mHeaderView.getVisiableHeight() : h;
        if (LogUtils.DEBUG) {
            // 因为是方法调用，所以[0]是这行代码的位置，[1]是最近一次调用者的位置
            final StackTraceElement[] tmpStaTraEle = (new Throwable())
                    .getStackTrace();
            final String flag = tmpStaTraEle[1].getFileName() + "->"
                    + tmpStaTraEle[1].getMethodName() + "->"
                    + tmpStaTraEle[1].getLineNumber() + ":";

            final String output = String.format("[%s] %s", flag,
                    "PullRefreshListView height == " + height + ", mPullRefreshing " + mPullRefreshing + ", mHeaderViewHeight " + mHeaderViewHeight);
            LogUtils.w(output);
        }
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        
        if (LogUtils.DEBUG) {
            // 因为是方法调用，所以[0]是这行代码的位置，[1]是最近一次调用者的位置
            final StackTraceElement[] tmpStaTraEle = (new Throwable())
                    .getStackTrace();
            final String flag = tmpStaTraEle[1].getFileName() + "->"
                    + tmpStaTraEle[1].getMethodName() + "->"
                    + tmpStaTraEle[1].getLineNumber() + ":";

            final String output = String.format("[%s] %s", flag,
                    "PullRefreshListView mPullRefreshing && height <= mHeaderViewHeight == " + (mPullRefreshing && height <= mHeaderViewHeight));
            LogUtils.w(output);
        }
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
        
        if (LogUtils.DEBUG) {
            // 因为是方法调用，所以[0]是这行代码的位置，[1]是最近一次调用者的位置
            final StackTraceElement[] tmpStaTraEle = (new Throwable())
                    .getStackTrace();
            final String flag = tmpStaTraEle[1].getFileName() + "->"
                    + tmpStaTraEle[1].getMethodName() + "->"
                    + tmpStaTraEle[1].getLineNumber() + ":";

            final String output = String.format("[%s] %s", flag,
                    "PullRefreshListView resetHeaderHeight == " + finalHeight + "-" + height);
            LogUtils.e(output);
        }
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(PullRefreshFooterView.STATE_READY);
            } else {
                mFooterView.setState(PullRefreshFooterView.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }
    
	public void startRefresh(final String timeMillis) {
		
		if (mHeaderViewHeight == 0) {
			mHeaderViewHeight = (int) getResources().getDimension(R.dimen.header_height);
		}
		
		if (!TextUtils.isEmpty(timeMillis)) {
			setRefreshTime(timeMillis);
		}
		
		mHeaderView.setVisiableHeight(mHeaderViewHeight);
		setSelection(0);
		mPullRefreshing = true;
		
		mHeaderView.setState(PullRefreshHeaderView.STATE_REFRESHING);
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}
		resetHeaderHeight();
	}

    public void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(PullRefreshFooterView.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 ||
                        deltaY > 0) && mEnablePullRefresh) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView
                        .getBottomMargin() > 0 || deltaY < 0) && isPullLoadFooterView) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(PullRefreshHeaderView.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA && isPullLoadFooterView) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
        
        // 滑动到底部自动触发加载更多
        if (isAutoPullLoad && mEnablePullLoad && scrollState == SCROLL_STATE_IDLE 
    			&& getLastVisiblePosition() == mTotalItemCount - 1 && isPullLoadFooterView) {
    		startLoadMore();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
        	mScrollDistance = computeVerticalScrollOffset();
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }
    
    @Override
    protected void layoutChildren() {
        
        try {
            super.layoutChildren();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
