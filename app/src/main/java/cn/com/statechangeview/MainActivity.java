package cn.com.statechangeview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cn.com.statechangeview.inject.MyActivity;
import cn.com.statechangeview.wrap.MyFragmentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvFragment = findViewById(R.id.tv_fragment);
        TextView tvActivity = findViewById(R.id.tv_Activity);

        tvFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyFragmentActivity.class));
            }
        });
        tvActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyActivity.class));
            }
        });
    }
}
