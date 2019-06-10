# StateManager

### 效果图如下
![state_manage](https://github.com/isJoker/StateManager/blob/master/app/gif/state_manage.gif)


### 初始化
```
stateManager = StateManager.manage(recyclerView)
                .setEmptyResource(R.layout.my_empty_layout)
                .setOnErrorClickListener(new StateManager.OnErrorClickListener() {
                    @Override
                    public void onErrorClick() {
                        stateManager.showLoading();
                        // TODO: 2019-06-10 retry to load data
                    }
                }).setOnInflateListener(new StateManager.OnInflateListener() {
                    @Override
                    public void onInflate(int viewType, View view) {
                        if (viewType == StateManagerType.EMPTY) {
                            TextView tvStatusEmptyContent = view.findViewById(R.id.tv_status_empty_content);
                            tvStatusEmptyContent.setText("这里空空如也~");
                        } else if (viewType == StateManagerType.ERROR) {
                            // TODO: 2019-06-10 错误页面UI处理
                        } else if (viewType == StateManagerType.LOADING) {
                            // TODO: 2019-06-10
                        }
                    }
                });
```
### 使用
```
        tvLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showLoading
                stateManager.showLoading();
            }
        });
        tvEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showEmpty
                stateManager.showEmpty();
            }
        });
        tvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showError
                stateManager.showError();
            }
        });
        tvNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showContent
                stateManager.showContent();
            }
        });
```
### 结合ViewModel + LiveData
#### AfterSaleRecordViewModel

```
public class AfterSaleRecordViewModel extends SBBaseViewModel {

    private MutableLiveData<Boolean> showEmpty = new MutableLiveData<>();
    private MutableLiveData<Boolean> showContent = new MutableLiveData<>();
    private MutableLiveData<Boolean> showError = new MutableLiveData<>();
    private MutableLiveData<Boolean> showLoading = new MutableLiveData<>();

    private List<AfterSaleItem> convertRecordData(long pageNo, List<AfterSaleRecordBean> beans) {
        // 根据页面的展示逻辑给设置不同的值
        // showLoading.postValue(isLoading);
        // showEmpty.postValue(isEmpty);
        // showError.postValue(isError);
        // showContent.postValue(isSuccess);
        return itemList;
    }

    public MutableLiveData<Boolean> getShowLoading() {
        return showLoading;
    }
    public MutableLiveData<Boolean> getShowEmpty() {
        return showEmpty;
    }
    public MutableLiveData<Boolean> getShowError() {
        return showError;
    }
    public MutableLiveData<Boolean> getShowContent() {
        return showContent;
    }

}

```

#### RecordDetailFragment
```
public class RecordDetailFragment extends SBBaseFragment {

    private AfterSaleFragmentDetailRecordBinding mBinding;
    private AfterSaleRecordViewModel mViewModel;
    private StateManager stateManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initStatusView();
    }

    private void initStatusView() {
        stateManager = StateManager.manage(mBinding.recyclerView)
                .setEmptyResource(R.layout.record_layout_empty)
                .setErrorResource(R.layout.record_layout_error)
                .setLoadingResource(R.layout.record_layout_loading)
                .setOnErrorClickListener(new StateManager.OnErrorClickListener() {
                    @Override
                    public void onErrorClick() {
                        stateManager.showLoading();
                        // TODO: 2019-06-10 处理重新请求数据逻辑
                    }
                }).setOnInflateListener(new StateManager.OnInflateListener() {
                    @Override
                    public void onInflate(int viewType, View view) {
                        if (viewType == StateManagerType.EMPTY) {
                            TextView tvStatusEmptyContent = view.findViewById(R.id.tv_status_empty_content);
                            tvStatusEmptyContent.setText("这里空空如也~");
                        } else if (viewType == StateManagerType.ERROR) {
                            // TODO: 2019-06-10 错误页面UI处理
                        } else if (viewType == StateManagerType.LOADING) {
                            // TODO: 2019-06-10
                        }
                    }
                });
        mViewModel.getShowEmpty().observe(this, isEmpty -> {
            if (isEmpty) {
                stateManager.showEmpty();
            }
        });
        mViewModel.getShowError().observe(this, isError -> {
            if (isError) {
                stateManager.showEmpty();
            }
        });
        mViewModel.getShowLoading().observe(this, isLoading -> {
            if (isLoading) {
                stateManager.showEmpty();
            }
        });
        mViewModel.getShowContent().observe(this, isSuccess -> {
            if (isSuccess) {
                stateManager.showEmpty();
            }
        });
    }
}
```
