package com.doc.saomiaodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private YcSaomiaoView mSaomiaoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        mSaomiaoView = (YcSaomiaoView) findViewById(R.id.saomiao);

    }
    public void click(View view){
        mSaomiaoView.startUp();
    }
    public void click2(View view){
        mSaomiaoView.end();
    }
}
