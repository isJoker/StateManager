package cn.com.state_library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by JokerWan on 2019-06-04.
 * Function: EMPTY/ERROR/LOADING/显示内容 四种状态控制
 */
public class StateManager extends View {

    private int mEmptyResource;
    private int mErrorResource;
    private int mLoadingResource;

    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    private OnErrorClickListener mErrorClickListener;
    private OnInflateListener mInflateListener;

    public StateManager(Context context) {
        this(context, null);
    }

    public StateManager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateManager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateManager);
        mEmptyResource = a.getResourceId(R.styleable.StateManager_emptyResource, 0);
        mErrorResource = a.getResourceId(R.styleable.StateManager_errorResource, 0);
        mLoadingResource = a.getResourceId(R.styleable.StateManager_loadingResource, 0);
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
            mEmptyView = inflate(mEmptyResource, StateManagerType.EMPTY);
        }

        showView(mEmptyView);
    }

    public void showError() {
        if (mErrorView == null) {
            mErrorView = inflate(mErrorResource, StateManagerType.ERROR);
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
            mLoadingView = inflate(mLoadingResource, StateManagerType.LOADING);
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

    private View inflate(@LayoutRes int layoutResource, @StateManagerType int viewType) {
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
                throw new IllegalArgumentException("StateManager must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("StateManager must have a non-null ViewGroup viewParent");
        }
    }


    /**
     * 包裹 view
     *
     * @param view 被包裹的view，只能为非跟布局的View
     */
    public static StateManager wrap(@NonNull View view) {
        FrameLayout wrap = new FrameLayout(view.getContext());
        wrap.setLayoutParams(view.getLayoutParams());

        StateManager stateView = new StateManager(view.getContext());
        stateView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            int index = viewGroup.indexOfChild(view);
            viewGroup.removeView(view);

            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            wrap.addView(view);
            wrap.addView(stateView);

            if (viewGroup instanceof ConstraintLayout) {
                viewGroup.addView(wrap);
                changeChildrenConstraints(viewGroup, wrap, view.getId());
            } else if (viewGroup instanceof LinearLayout) {
                viewGroup.addView(wrap, index);
            } else {
                viewGroup.addView(wrap);
            }
        } else {
            throw new ClassCastException("view or view.getParent() must be ViewGroup");
        }
        return stateView;
    }

    /**
     * 设置 emptyView 的自定义 Layout
     *
     * @param emptyResource emptyView 的 layoutResource
     */
    public StateManager setEmptyResource(@LayoutRes int emptyResource) {
        this.mEmptyResource = emptyResource;
        return this;
    }

    /**
     * 设置 errorView 的自定义 Layout
     *
     * @param errorResource errorView 的 layoutResource
     */
    public StateManager setErrorResource(@LayoutRes int errorResource) {
        this.mErrorResource = errorResource;
        return this;
    }

    /**
     * 设置 loadingView 的自定义 Layout
     *
     * @param loadingResource loadingView 的 layoutResource
     */
    public StateManager setLoadingResource(@LayoutRes int loadingResource) {
        mLoadingResource = loadingResource;
        return this;
    }


    public StateManager setOnErrorClickListener(OnErrorClickListener listener) {
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
    public StateManager setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
        return this;
    }

    public interface OnInflateListener {
        void onInflate(@StateManagerType int viewType, View view);
    }
}
