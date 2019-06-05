package cn.com.state_library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by JokerWan on 2019-06-04.
 * Function: EMPTY/ERROR/LOADING/显示内容 四种状态控制
 */
public class StateManagerView extends View {

    private int mEmptyResource;
    private int mErrorResource;
    private int mLoadingResource;

    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    private OnErrorClickListener mErrorClickListener;
    private OnInflateListener mInflateListener;

    public StateManagerView(Context context) {
        this(context, null);
    }

    public StateManagerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateManagerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateManagerView);
        mEmptyResource = a.getResourceId(R.styleable.StateManagerView_emptyResource, 0);
        mErrorResource = a.getResourceId(R.styleable.StateManagerView_errorResource, 0);
        mLoadingResource = a.getResourceId(R.styleable.StateManagerView_loadingResource, 0);
        a.recycle();

        if (mEmptyResource == 0) {
            mEmptyResource = R.layout.state_empty;
        }
        if (mErrorResource == 0) {
            mErrorResource = R.layout.state_error;
        }
        if (mLoadingResource == 0) {
            mLoadingResource = R.layout.state_loading;
        }

        setVisibility(GONE);
        setWillNotDraw(true);
    }

    private static StateManagerView build(ViewGroup parent) {
        int screenHeight = 0;
        if (parent instanceof LinearLayout ||
                parent instanceof ScrollView ||
                parent instanceof AdapterView ||
                (parent instanceof ScrollingView && parent instanceof NestedScrollingChild) ||
                (parent instanceof NestedScrollingParent && parent instanceof NestedScrollingChild)) {
            ViewParent viewParent = parent.getParent();
            if (viewParent == null) { // 顶层布局
                // 创建一个 FrameLayout 包裹 StateManagerView 和 parent's childView
                FrameLayout wrapper = new FrameLayout(parent.getContext());
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                wrapper.setLayoutParams(layoutParams);

                if (parent instanceof LinearLayout) {
                    // 创建一个 LinearLayout 包裹 parent's childView
                    LinearLayout wrapLayout = new LinearLayout(parent.getContext());
                    wrapLayout.setLayoutParams(parent.getLayoutParams());
                    wrapLayout.setOrientation(((LinearLayout) parent).getOrientation());

                    for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
                        View childView = parent.getChildAt(0);
                        parent.removeView(childView);
                        wrapLayout.addView(childView);
                    }
                    wrapper.addView(wrapLayout);
                } else if (parent instanceof ScrollView || parent instanceof ScrollingView) {
                    if (parent.getChildCount() != 1) {
                        throw new IllegalStateException("the ScrollView does not have one direct child");
                    }
                    View directView = parent.getChildAt(0);
                    parent.removeView(directView);
                    wrapper.addView(directView);

                    WindowManager wm = (WindowManager) parent.getContext()
                            .getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics metrics = new DisplayMetrics();
                    if (wm != null) {
                        wm.getDefaultDisplay().getMetrics(metrics);
                    }
                    screenHeight = metrics.heightPixels;
                } else if (parent instanceof NestedScrollingParent &&
                        parent instanceof NestedScrollingChild) {
                    if (parent.getChildCount() == 2) {
                        View targetView = parent.getChildAt(1);
                        parent.removeView(targetView);
                        wrapper.addView(targetView);
                    } else if (parent.getChildCount() > 2) {
                        throw new IllegalStateException("the view is not refresh layout? view = "
                                + parent.toString());
                    }
                } else {
                    throw new IllegalStateException("the view does not have parent, view = "
                            + parent.toString());
                }

                // 将wrapper添加到parent
                parent.addView(wrapper);
                parent = wrapper;
            } else {
                FrameLayout root = new FrameLayout(parent.getContext());
                root.setLayoutParams(parent.getLayoutParams());

                if (viewParent instanceof ViewGroup) {
                    ViewGroup rootGroup = (ViewGroup) viewParent;
                    // 把 parent 从它自己的父容器中移除
                    rootGroup.removeView(parent);
                    // 然后替换成新的
                    rootGroup.addView(root);

                    if (rootGroup instanceof ConstraintLayout) {
                        changeChildrenConstraints(rootGroup, root, parent.getId());
                    }
                }

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                parent.setLayoutParams(layoutParams);
                root.addView(parent);
                parent = root;
            }
        }
        StateManagerView StateManagerView = new StateManagerView(parent.getContext());
        if (screenHeight > 0) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, screenHeight);
            parent.addView(StateManagerView, params);
        } else {
            parent.addView(StateManagerView);
        }
        StateManagerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        StateManagerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        return StateManagerView;
    }

    public static void changeChildrenConstraints(ViewGroup viewParent, FrameLayout root, int injectViewId) {
        int rootId = R.id.root_id;
        root.setId(rootId);
        ConstraintLayout rootGroup = ((ConstraintLayout) viewParent);
        for (int i = 0, count = rootGroup.getChildCount(); i < count; i++) {
            View child = rootGroup.getChildAt(i);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) child.getLayoutParams();
            if (layoutParams.circleConstraint == injectViewId) {
                layoutParams.circleConstraint = rootId;
            } else {
                if (layoutParams.leftToLeft == injectViewId) {
                    layoutParams.leftToLeft = rootId;
                } else if (layoutParams.leftToRight == injectViewId) {
                    layoutParams.leftToRight = rootId;
                }

                if (layoutParams.rightToLeft == injectViewId) {
                    layoutParams.rightToLeft = rootId;
                } else if (layoutParams.rightToRight == injectViewId) {
                    layoutParams.rightToRight = rootId;
                }

                if (layoutParams.topToTop == injectViewId) {
                    layoutParams.topToTop = rootId;
                } else if (layoutParams.topToBottom == injectViewId) {
                    layoutParams.topToBottom = rootId;
                }

                if (layoutParams.bottomToTop == injectViewId) {
                    layoutParams.bottomToTop = rootId;
                } else if (layoutParams.bottomToBottom == injectViewId) {
                    layoutParams.bottomToBottom = rootId;
                }

                if (layoutParams.baselineToBaseline == injectViewId) {
                    layoutParams.baselineToBaseline = rootId;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    @Override
    public void setVisibility(int visibility) {
        setVisibility(mEmptyView, visibility);
        setVisibility(mErrorView, visibility);
        setVisibility(mLoadingView, visibility);
    }

    private void setVisibility(View view, int visibility) {
        if (view != null && visibility != view.getVisibility()) {
            view.setVisibility(visibility);
        }
    }

    public void showContent() {
        setVisibility(GONE);
    }

    public void showEmpty() {
        if (mEmptyView == null) {
            mEmptyView = inflate(mEmptyResource, StateManagerViewType.EMPTY);
        }

        showView(mEmptyView);
    }

    public void showError() {
        if (mErrorView == null) {
            mErrorView = inflate(mErrorResource, StateManagerViewType.ERROR);
            mErrorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mErrorClickListener != null) {
                        showLoading();
                        mErrorClickListener.onErrorClick();
                    }
                }
            });
        }

        showView(mErrorView);
    }

    public void showLoading() {
        if (mLoadingView == null) {
            mLoadingView = inflate(mLoadingResource, StateManagerViewType.LOADING);
        }

        showView(mLoadingView);
    }

    private void showView(View view) {
        setVisibility(view, VISIBLE);
        hideViews(view);
    }

    private void hideViews(View showView) {
        if (mEmptyView == showView) {
            setVisibility(mLoadingView, GONE);
            setVisibility(mErrorView, GONE);
        } else if (mLoadingView == showView) {
            setVisibility(mEmptyView, GONE);
            setVisibility(mErrorView, GONE);
        } else {
            setVisibility(mEmptyView, GONE);
            setVisibility(mLoadingView, GONE);
        }
    }

    private View inflate(@LayoutRes int layoutResource, @StateManagerViewType int viewType) {
        final ViewParent viewParent = getParent();

        if (viewParent instanceof ViewGroup) {
            if (layoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final LayoutInflater factory = LayoutInflater.from(getContext());
                final View view = factory.inflate(layoutResource, parent, false);

                final int index = parent.indexOfChild(this);
                // 防止还能触摸底下的 View
                view.setClickable(true);
                // 先不显示
                view.setVisibility(GONE);

                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams != null) {
                    parent.addView(view, index, layoutParams);
                } else {
                    parent.addView(view, index);
                }

                if (mLoadingView != null && mErrorView != null && mEmptyView != null) {
                    parent.removeViewInLayout(this);
                }

                if (mInflateListener != null) {
                    mInflateListener.onInflate(viewType, view);
                }

                return view;
            } else {
                throw new IllegalArgumentException("StateManagerView must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("StateManagerView must have a non-null ViewGroup viewParent");
        }
    }

    /**
     * 管理view
     *
     * @param view 要管理的View
     * @return StateManagerView
     */
    public static StateManagerView manage(@NonNull View view) {
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            return build(parent);
        } else {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                return build((ViewGroup) parent);
            } else {
                throw new ClassCastException("view or view.getParent() must be ViewGroup");
            }
        }
    }

    /**
     * 设置 emptyView 的自定义 Layout
     *
     * @param emptyResource emptyView 的 layoutResource
     */
    public StateManagerView setEmptyResource(@LayoutRes int emptyResource) {
        this.mEmptyResource = emptyResource;
        return this;
    }

    /**
     * 设置 errorView 的自定义 Layout
     *
     * @param errorResource errorView 的 layoutResource
     */
    public StateManagerView setErrorResource(@LayoutRes int errorResource) {
        this.mErrorResource = errorResource;
        return this;
    }

    /**
     * 设置 loadingView 的自定义 Layout
     *
     * @param loadingResource loadingView 的 layoutResource
     */
    public StateManagerView setLoadingResource(@LayoutRes int loadingResource) {
        mLoadingResource = loadingResource;
        return this;
    }


    public StateManagerView setOnErrorClickListener(OnErrorClickListener listener) {
        this.mErrorClickListener = listener;
        return this;
    }

    public interface OnErrorClickListener {
        void onErrorClick();
    }

    /**
     * 渲染完成监听，可用来改变几种状态视图
     *
     * @param inflateListener inflateListener
     */
    public StateManagerView setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
        return this;
    }

    public interface OnInflateListener {
        void onInflate(@StateManagerViewType int viewType, View view);
    }
}
