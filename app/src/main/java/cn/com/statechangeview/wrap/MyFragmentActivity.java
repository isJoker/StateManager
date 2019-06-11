package cn.com.statechangeview.wrap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.com.statechangeview.R;

public class MyFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fragment);

        getSupportFragmentManager().beginTransaction().add(R.id.container,new MyFragment()).commit();
    }
}
