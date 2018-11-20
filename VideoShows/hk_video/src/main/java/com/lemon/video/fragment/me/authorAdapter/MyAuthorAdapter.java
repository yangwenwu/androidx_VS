package com.lemon.video.fragment.me.authorAdapter;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemon.video.R;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

public class MyAuthorAdapter extends AuthorizeAdapter {

    @Override
    public void onCreate() {
        hideShareSDKLogo();
        super.onCreate();
        TitleLayout llTitle = getTitleLayout();
        TextView textView = llTitle.getTvTitle();
        textView.setGravity(Gravity.CENTER);
        llTitle.getChildAt(1).setVisibility(View.GONE);
        llTitle.getChildAt(2).setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        int rightPadding = DensityUtil.dp2px(48);
        textView.setPadding(0, 0, rightPadding, 0);

        ImageView back = llTitle.getBtnBack();
        back.setImageResource(R.mipmap.back_arrow3);
//        ViewGroup.LayoutParams params = back.getLayoutParams();
//        params.height= DensityUtil.dp2px(25);
//        params.width =DensityUtil.dp2px(25);
//        back.setLayoutParams(params);

    }


}
