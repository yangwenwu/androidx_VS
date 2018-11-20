package com.lemon.video.fragment.me.activity;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;

public class AboutUsActivity extends BaseAppActivity {
    private TextView tv3_content2, tv3_content1,
            tv2_content4, tv2_content3, tv2_content2, tv2_content1,
            tv1_content4, tv1_content3, tv1_content2, tv1_content1;
    private TextView tv3_top, tv2_top, tv1_top;
    private ImageView nav_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        //由于6.0主题添加 name="android:windowTranslucentStatus">true属性的原因，这里需要垫上状态栏的高度
        initView();
    }

    private void initView() {
        AssetManager mgr = getAssets();
        //根据路径得到Typeface
        Typeface regular = Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf");
//        app_description.setTypeface(tf2);
        nav_back = (ImageView) findViewById(R.id.nav_back);
        nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv3_content2 = (TextView) findViewById(R.id.tv3_content2);
        tv3_content1 = (TextView) findViewById(R.id.tv3_content1);
        tv3_content2.setTypeface(regular);
        tv3_content1.setTypeface(regular);

        tv2_content4 = (TextView) findViewById(R.id.tv2_content4);
        tv2_content3 = (TextView) findViewById(R.id.tv2_content3);
        tv2_content2 = (TextView) findViewById(R.id.tv2_content2);
        tv2_content1 = (TextView) findViewById(R.id.tv2_content1);
        tv2_content4.setTypeface(regular);
        tv2_content3.setTypeface(regular);
        tv2_content2.setTypeface(regular);
        tv2_content1.setTypeface(regular);


        tv1_content4 = (TextView) findViewById(R.id.tv1_content4);
        tv1_content3 = (TextView) findViewById(R.id.tv1_content3);
        tv1_content2 = (TextView) findViewById(R.id.tv1_content2);
        tv1_content1 = (TextView) findViewById(R.id.tv1_content1);
        tv1_content4.setTypeface(regular);
        tv1_content3.setTypeface(regular);
        tv1_content2.setTypeface(regular);
        tv1_content1.setTypeface(regular);

        tv3_top = (TextView) findViewById(R.id.tv3_top);
        tv2_top = (TextView) findViewById(R.id.tv2_top);
        tv1_top = (TextView) findViewById(R.id.tv1_top);
        tv3_top.setTypeface(regular);
        tv2_top.setTypeface(regular);
        tv1_top.setTypeface(regular);
    }

}
