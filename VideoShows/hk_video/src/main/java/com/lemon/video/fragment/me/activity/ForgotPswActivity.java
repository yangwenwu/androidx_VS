package com.lemon.video.fragment.me.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.common.Commrequest;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPswActivity extends BaseAppActivity implements View.OnClickListener {
    private EditText email_account;
    private ImageView nav_back;
    private TextView send_psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将5.0系统版本的状态栏变成透明色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && 23 > Build.VERSION.SDK_INT) {
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
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        setContentView(R.layout.about_us);
        setContentView(R.layout.forget_psw);
        //由于6.0主题添加 name="android:windowTranslucentStatus">true属性的原因，这里需要垫上状态栏的高度
        initSystemBar();
        initView();

    }

    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TextView textView = (TextView) findViewById(R.id.text);
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

    private void initView() {
        email_account = (EditText) findViewById(R.id.email_account);
        send_psw = (TextView) findViewById(R.id.send_psw);
        nav_back = (ImageView) findViewById(R.id.nav_back);
        nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send_psw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_psw:
                String email = email_account.getText().toString();
                if (email != null && !email.equals("")) {
                    if (isEmail(email)) {
                        sendEmailCode(email);
                    } else {
                        ToastUtils.showShort(ForgotPswActivity.this, "Please enter correct email");
                    }
                }
                break;
        }
    }

    public boolean isEmail(String eMAIL1) {
        Pattern pattern = Pattern
                .compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
        Matcher mc = pattern.matcher(eMAIL1);
        return mc.matches();
    }

    /***
     * 找回密码
     * @param email
     */
    private void sendEmailCode(final String email) {
        Commrequest.sendCode(ForgotPswActivity.this, email, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {

                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")) {
                    JSONObject resObject = jsonObject.getJSONObject("resObject");
                    ToastUtils.showShort(ForgotPswActivity.this, getResources().getString(R.string.code_send_success));
                    //发送成功之后进行跳转，跳到成功界面
                    Intent intent = new Intent(ForgotPswActivity.this, SendPswOkActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.showShort(ForgotPswActivity.this, resMsg);
                }

            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                ToastUtils.showShort(ForgotPswActivity.this, getResources().getString(R.string.code_send_failed));
            }
        });
    }
}
