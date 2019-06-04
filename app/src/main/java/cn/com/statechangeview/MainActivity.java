package cn.com.statechangeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.com.state_library.StateManagerView;
import cn.com.state_library.StateManagerViewType;

public class MainActivity extends AppCompatActivity {

    private StateManagerView stateManagerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvLoading = findViewById(R.id.tv_loading);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        TextView tvError = findViewById(R.id.tv_error);
        TextView tvNormal = findViewById(R.id.tv_normal);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(new MyAdapter());

        stateManagerView = StateManagerView.manage(recyclerView)
                .setEmptyResource(R.layout.my_empty_layout)
                .setOnErrorClickListener(new StateManagerView.OnErrorClickListener() {
                    @Override
                    public void onErrorClick() {
                        stateManagerView.showLoading();
                    }
                }).setOnInflateListener(new StateManagerView.OnInflateListener() {
                    @Override
                    public void onInflate(int viewType, View view) {
                        if (viewType == StateManagerViewType.EMPTY) {
                            TextView tvStatusEmptyContent = view.findViewById(R.id.tv_status_empty_content);
                            tvStatusEmptyContent.setText("这里空空如也~");
                        }
                    }
                });


        tvLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManagerView.showLoading();
            }
        });
        tvEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManagerView.showEmpty();
            }
        });
        tvError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManagerView.showError();
            }
        });
        tvNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateManagerView.showContent();
            }
        });
    }
}
