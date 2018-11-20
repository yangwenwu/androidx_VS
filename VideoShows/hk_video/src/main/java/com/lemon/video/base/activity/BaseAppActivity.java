package com.lemon.video.base.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.lemon.video.log.CommonLog;
import com.lemon.video.log.LogFactory;
import com.umeng.analytics.MobclickAgent;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseAppActivity extends AppCompatActivity {
    protected CommonLog log = LogFactory.createLog();
    private long click = 0;// 限制点击过快重复打开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void startActivity(Intent intent) {
        if (System.currentTimeMillis() - click > 0 && System.currentTimeMillis() - click < 500) {
            click = System.currentTimeMillis();
            return;
        }
        click = System.currentTimeMillis();
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            log.i("we can not find the activity !!!" + e.toString());
        }
    }

    /**
     * 这个方法是截止外部的字体设置
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        try {
            res.updateConfiguration(config, res.getDisplayMetrics());
        } catch (Exception e) {
            log.e("Exception e: " + e.toString());
        }
        return res;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}