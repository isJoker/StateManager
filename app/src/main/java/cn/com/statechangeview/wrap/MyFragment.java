package cn.com.statechangeview.wrap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.state_library.StateManager;
import cn.com.state_library.StateManagerType;
import cn.com.statechangeview.R;

/**
 * Created by JokerWan on 2019-06-10.
 * Function:
 */
public class MyFragment extends Fragment {

    private View inflate;
    private StateManager stateManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_my, container, false);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView tvLoading = inflate.findViewById(R.id.tv_loading);
        TextView tvEmpty = inflate.findViewById(R.id.tv_empty);
        TextView tvError = inflate.findViewById(R.id.tv_error);
        TextView tvNormal = inflate.findViewById(R.id.tv_normal);
        TextView tvContent = inflate.findViewById(R.id.tv_content);
        TextView tvBottom = inflate.findViewById(R.id.tv_bottom);

        stateManager = StateManager.wrap(tvContent)
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


        tvLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManager.showLoading();
            }
        });
        tvEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManager.showEmpty();
            }
        });
        tvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManager.showError();
            }
        });
        tvNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManager.showContent();
            }
        });
    }
}
