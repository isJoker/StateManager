package cn.com.statechangeview.inject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.com.state_library.StateManager;
import cn.com.state_library.StateManagerType;
import cn.com.statechangeview.R;

public class MyActivity extends AppCompatActivity {

    private StateManager stateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        TextView tvLoading = findViewById(R.id.tv_loading);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        TextView tvError = findViewById(R.id.tv_error);
        TextView tvNormal = findViewById(R.id.tv_normal);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(new MyAdapter());

        stateManager = StateManager.inject(recyclerView)
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
