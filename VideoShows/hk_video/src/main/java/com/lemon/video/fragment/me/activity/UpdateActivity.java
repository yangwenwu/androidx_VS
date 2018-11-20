package com.lemon.video.fragment.me.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;


public class UpdateActivity extends BaseAppActivity {
    private TextView app_version,user_terms,textView3;
    private ImageView nav_back;
//    private CBAlignTextView mCbAlignTv;
    private TextView app_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_layout); //about_us
        //由于6.0主题添加 name="android:windowTranslucentStatus">true属性的原因，这里需要垫上状态栏的高度
        initView();
        try {
            setVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView(){
        app_description = (TextView) findViewById(R.id.app_description);
        textView3 = (TextView) findViewById(R.id.textView3);
        AssetManager mgr = getAssets();
        //根据路径得到Typeface
        Typeface tf2=Typeface.createFromAsset(mgr, "fonts/Lato-Bold.ttf");
        Typeface tf3=Typeface.createFromAsset(mgr, "fonts/Lato-Regular.ttf");
        textView3.setTypeface(tf2);
//        app_description.setTypeface(tf2);
//        textView3.setText("China Daily News is up to date");
        nav_back = (ImageView) findViewById(R.id.nav_back);
        app_version = (TextView) findViewById(R.id.app_version);
        app_version.setTypeface(tf3);
        nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setVersion()throws Exception{
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        app_version.setText("Version："+version);

    }



}
