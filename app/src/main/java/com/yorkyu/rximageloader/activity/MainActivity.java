package com.yorkyu.rximageloader.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yorkyu.rximageloader.R;
import com.yorkyu.rximageloader.loader.RxImageLoader;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Button bt;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
        bt = (Button) findViewById(R.id.bt);
        url = "http://img2.baa.bitautotech.com/usergroup/editor_pic/2017/3/22/694494c2f3544226ae911bf86b4e2bcc.png";

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxImageLoader.with(MainActivity.this).load(url).into(iv);
            }
        });
    }
}
