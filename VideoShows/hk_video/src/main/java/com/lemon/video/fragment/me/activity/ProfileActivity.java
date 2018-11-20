package com.lemon.video.fragment.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.model.UserBean;
import com.lemon.video.utils.SPUtils;

public class ProfileActivity extends BaseAppActivity implements View.OnClickListener{

    private RelativeLayout nickname_layout,profile_password;
    private ImageView back;
    private TextView profile_nickname,gap_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        back = (ImageView) findViewById(R.id.back);
        profile_nickname = (TextView) findViewById(R.id.profile_nickname);
        gap_line = (TextView) findViewById(R.id.gap_line);
        nickname_layout = (RelativeLayout) findViewById(R.id.nickname_layout);
        profile_password = (RelativeLayout) findViewById(R.id.profile_password);
        nickname_layout.setOnClickListener(this);
        profile_password.setOnClickListener(this);
        back.setOnClickListener(this);
        setNickname();
    }

    private void setNickname(){
        String userStr = (String) SPUtils.get(ProfileActivity.this,"user","");
        UserBean userBean = JSON.parseObject(userStr,UserBean.class);
        profile_nickname.setText(userBean.nickName);
        if (userBean.account != null && !userBean.account.equals("")){
            profile_password.setVisibility(View.VISIBLE);
            gap_line.setVisibility(View.VISIBLE);
        }else{
            profile_password.setVisibility(View.GONE);
            gap_line.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nickname_layout:
                Intent intent = new Intent(ProfileActivity.this,ChangeNicknameActivity.class);
                startActivity(intent);
                setRefreshLister();
                break;
            case R.id.profile_password:
                Intent intent1 = new Intent(ProfileActivity.this,ChangePswActivity.class);
                startActivity(intent1);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void setRefreshLister(){
        ChangeNicknameActivity.readingRefreshListen(new ChangeNicknameActivity.ReadingTypeRefreshListen() {
            @Override
            public void reFresh() {
                setNickname();
            }
        });
    }
}
