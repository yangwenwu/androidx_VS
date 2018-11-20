package com.lemon.video.fragment.me.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;

public class SendPswOkActivity extends BaseAppActivity implements View.OnClickListener{
    private TextView ok_tb,info_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将5.0系统版本的状态栏变成透明色
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && 23 >Build.VERSION.SDK_INT) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
//            window.setStatusBarColor(Color.parseColor("#cd020a"));
        }

        //6.0的设置状态栏字体颜色，背景为淡色时，字体就是深色，背景为深色时，字体颜色就是白色
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        setContentView(R.layout.about_us);
        setContentView(R.layout.sen_psw_ok_layout);
        //由于6.0主题添加 name="android:windowTranslucentStatus">true属性的原因，这里需要垫上状态栏的高度
        initSystemBar();
        initView();

    }

    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TextView textView =  (TextView)findViewById(R.id.text);
            textView.setHeight(getStatusBarHeight());
        }
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initView(){
        ok_tb = (TextView) findViewById(R.id.ok_tb);
        info_text = (TextView) findViewById(R.id.info_text);
        ok_tb.setOnClickListener(this);

        String email =getIntent().getStringExtra("email");
        info_text.setText("Please sign in your e-mail "+ email +" to check your new password");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok_tb:
                finish();
                break;
        }
    }
}
