package com.lemon.video.fragment.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.common.Commrequest;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.fragment.me.MineFragment;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.UserBean;
import com.lemon.video.utils.SPUtils;
import com.lemon.video.utils.ToastUtil;

public class ChangeNicknameActivity extends BaseAppActivity implements View.OnClickListener {

    private EditText nickname;
    private TextView submit_bt;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nickname_layout);
        back = (ImageView) findViewById(R.id.back);
        nickname = (EditText) findViewById(R.id.nickname);
        submit_bt = (TextView) findViewById(R.id.submit_bt);
        submit_bt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_bt:
                String newName = nickname.getText().toString();
                if (newName != null && !newName.equals("")) {
                    changeName(newName);
                }
                break;
            case R.id.back:
                finish();
                break;

        }
    }

    private void changeName(String nickname) {
        String userStr = (String) SPUtils.get(ChangeNicknameActivity.this, "user", "");
        UserBean userBean = JSON.parseObject(userStr, UserBean.class);
        String id = userBean.id;
        Commrequest.changeNickName(ChangeNicknameActivity.this, id, nickname, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {

                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")) {
                    JSONObject resObject = jsonObject.getJSONObject("resObject");
                    String newNickName = resObject.getString("nickName");
//                    UserBean userBean = JSON.parseObject(resObject.toJSONString(), UserBean.class);
                    String userStr = (String) SPUtils.get(ChangeNicknameActivity.this, "user", "");
                    UserBean userBean = JSON.parseObject(userStr, UserBean.class);
                    userBean.nickName = newNickName;

                    //转json字符串
                    String jsonObject1 = JSON.toJSONString(userBean);
                    SPUtils.put(ChangeNicknameActivity.this, "user", jsonObject1);
                    sendNicknameStatuBroadcast();
//                    ToastUtils.showShort(ChangeNicknameActivity.this,"修改成功");
                    if (readingTypeRefreshListen != null) {
                        readingTypeRefreshListen.reFresh();
                        readingTypeRefreshListen = null;
                    }
                    finish();
                } else {
                    ToastUtil.show("modify failed");
                }

            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                ToastUtil.show("modify failed");
            }
        });
    }

    //昵称修改成功发送广播
    private void sendNicknameStatuBroadcast() {
        Intent it = new Intent(MineFragment.NICKNAME_NOTICE);
        sendBroadcast(it);
    }


    public static ReadingTypeRefreshListen readingTypeRefreshListen;

    public interface ReadingTypeRefreshListen {
        void reFresh();
    }

    public static void readingRefreshListen(ReadingTypeRefreshListen readingTypeRefreshListen) {
        ChangeNicknameActivity.readingTypeRefreshListen = readingTypeRefreshListen;
    }
}
