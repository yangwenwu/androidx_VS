package com.lemon.video.fragment.me.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.common.Commrequest;
import com.lemon.video.entrys.BaseJsonBean;
import com.lemon.video.httpModel.FeedbackModel;
import com.lemon.video.https.ResponseListener;
import com.lemon.video.utils.NetWorkUtil;
import com.lemon.video.utils.QuickClick2TimesUtil;
import com.lemon.video.utils.ToastUtils;

public class FeedBackActivity extends BaseAppActivity implements View.OnClickListener {

    private ImageView nav_back;
    private TextView submit;
    private EditText feedback_content, feedback_phone;

    //不超过500字
    private final static int CHAR_MAX_NUM = 500;
    private TextView user_feedback_num;
    private FrameLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        initView();
    }

    private void initView() {
        loading = (FrameLayout) findViewById(R.id.loading);
        //提交内容
        feedback_content = (EditText) findViewById(R.id.feedback_content);
        //提交的联系方式
        feedback_phone = (EditText) findViewById(R.id.feedback_phone);
        user_feedback_num = (TextView) findViewById(R.id.user_feedback_num);

        nav_back = (ImageView) findViewById(R.id.nav_back);
        nav_back.setOnClickListener(this);
        //提交按钮
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);

        feedback_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > CHAR_MAX_NUM) {
                    ToastUtils.showShort(FeedBackActivity.this, "The word count is over the limit ");//字数已经超过限制
                    s = s.subSequence(0, CHAR_MAX_NUM);
                    feedback_content.setText(s);
                    feedback_content.setSelection(s.length());
                }
                user_feedback_num.setText(s.length() + "/" + CHAR_MAX_NUM);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_back:  //返回
                finish();
                break;
            case R.id.submit:  //保存修改
                if (QuickClick2TimesUtil.isFastClick()) {
                    String content = feedback_content.getText().toString();
                    if (content != null && !content.equals("")) {
                        if (NetWorkUtil.isNetworkAvailable(FeedBackActivity.this)) {
                            //提交
                            submitFB(content);
                        } else {
                            ToastUtils.showShort(FeedBackActivity.this, getResources().getString(R.string.net_error));
                            return;
                        }

                    } else {
                        ToastUtils.showShort(FeedBackActivity.this, "Please input your feedback");
                        return;
                    }
                }
                break;
        }
    }

    private void submitFB( String content){
        String contact = feedback_phone.getText().toString();
//        Commrequest.submitFeedBack(FeedBackActivity.this,content,contact , new ResponseListener() {
//            @Override
//            public void onResponse(BaseJsonBean t, int code) {
//                // 反馈结果{"resCode":"200","resMsg":"success","resObject":null}
//                JSONObject jsonObject = JSON.parseObject(t.object);
//                String resMsg = jsonObject.getString("resMsg");
//                if (resMsg.equals("success")){
//                    finish();
//                }else{
//                    ToastUtils.showShort(FeedBackActivity.this, "submit fail");
//                }
//            }
//
//            @Override
//            public void onFailure(BaseJsonBean t, String errMessage) {
//                ToastUtils.showShort(FeedBackActivity.this, "submit fail");
//            }
//        });

        FeedbackModel model = new FeedbackModel();
        model.content = content;
        model.email = contact;
        Commrequest.submitFeedBack1(FeedBackActivity.this, model , new ResponseListener() {
            @Override
            public void onResponse(BaseJsonBean t, int code) {
                // 反馈结果{"resCode":"200","resMsg":"success","resObject":null}
                JSONObject jsonObject = JSON.parseObject(t.object);
                String resMsg = jsonObject.getString("resMsg");
                if (resMsg.equals("success")){
                    finish();
                }else{
                    ToastUtils.showShort(FeedBackActivity.this, "Submit failed");
                }
            }

            @Override
            public void onFailure(BaseJsonBean t, String errMessage) {
                ToastUtils.showShort(FeedBackActivity.this, "Submit failed");
            }
        });

    }


}
