package com.lemon.video.fragment.me.activity;

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
import com.lemon.video.https.ResponseListener;
import com.lemon.video.model.UserBean;
import com.lemon.video.utils.SPUtils;
import com.lemon.video.utils.ToastUtil;
import com.lemon.video.utils.ToastUtils;

public class ChangePswActivity extends BaseAppActivity implements View.OnClickListener{

    private EditText old_psw,newpsw1,newpsw2;
    private TextView submit_bt;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepsw_layout);
        back = (ImageView) findViewById(R.id.back);
        old_psw = (EditText) findViewById(R.id.old_psw);
        newpsw1 = (EditText) findViewById(R.id.newpsw1);
        newpsw2 = (EditText) findViewById(R.id.newpsw2);
        submit_bt = (TextView) findViewById(R.id.submit_bt);
        submit_bt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.submit_bt:
                String oldPsw = old_psw.getText().toString();
                String new_Psw1 = newpsw1.getText().toString();
                String new_Psw2 = newpsw2.getText().toString();
                if (oldPsw != null && !oldPsw.equals("")){
                    if (new_Psw1 != null && !new_Psw1.equals("")){
                        if (new_Psw2 != null && !new_Psw2.equals("")){

                            if (new_Psw1.equals(new_Psw2)){
                                changePsw(new_Psw1,oldPsw);
                            }else{
                                ToastUtils.showShort(ChangePswActivity.this,"Passwords don’t match");
                            }
                        }else{
                            ToastUtils.showShort(ChangePswActivity.this,"Please confirm your new password");
                        }

                    }else{
                        ToastUtils.showShort(ChangePswActivity.this,"Please enter your new password");
                    }
                }else{
                    ToastUtils.showShort(ChangePswActivity.this,"Please enter your current password");
                }
                break;
        }
    }

    private void changePsw(String newPsw,String oldPsw ){
        String userStr = (String) SPUtils.get(ChangePswActivity.this,"user","");
        UserBean userBean = JSON.parseObject(userStr,UserBean.class);
        String id = userBean.id;
        Commrequest.changePsw(ChangePswActivity.this, id,newPsw,oldPsw, new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {

                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")){
                    JSONObject resObject = jsonObject.getJSONObject("resObject");
//                    UserBean userBean = JSON.parseObject(resObject.toJSONString(), UserBean.class);
//                    ToastUtils.showShort(ChangePswActivity.this,"修改成功");
                    finish();
                }else{
//                    ToastUtils.showShort(ChangePswActivity.this,resMsg);
                    ToastUtil.show("modify failed");
                }

            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                ToastUtil.show("modify failed");
            }
        });
    }
}
