package com.lemon.video.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lemon.video.R;


/**
 * 自定义dialog
 */
public class MobShareDialog extends Dialog  {

	private LinearLayout facebook,twitter,linkedin,google,instagram,wechat,wechat_moment;

	private TextView cancel;
	public MobShareDialog(Context context) {
		super(context, R.style.DialogWithDim);
		setDialog();
	}

	private void setDialog() {
//		View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.share_popup_layout, null);
		facebook = (LinearLayout) mView.findViewById(R.id.facebook);
		twitter = (LinearLayout) mView.findViewById(R.id.twitter);
		linkedin = (LinearLayout) mView.findViewById(R.id.linkedin);

		google = (LinearLayout) mView.findViewById(R.id.google);
		instagram = (LinearLayout) mView.findViewById(R.id.instagram);
		wechat = (LinearLayout) mView.findViewById(R.id.wechat);
		wechat_moment = (LinearLayout) mView.findViewById(R.id.wechat_moment);
		cancel = (TextView) mView.findViewById(R.id.cancel);
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		super.setContentView(mView);

		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics size = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(size);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (size.widthPixels * 1); // 高度设置为屏幕的0.9
//		p.height = (int) (size.heightPixels * 0.7); //高度设置为屏幕的0.7
		dialogWindow.setAttributes(p);
	}

	public void setFacebookListener(View.OnClickListener listener) {
		facebook.setOnClickListener(listener);
	}

	public void setTwitterListener(View.OnClickListener listener) {
		twitter.setOnClickListener(listener);
	}

	public void setLinkedinListener(View.OnClickListener listener) {
		linkedin.setOnClickListener(listener);
	}

	public void setGoogleListener(View.OnClickListener listener) {
		google.setOnClickListener(listener);
	}

	public void setInstagramListener(View.OnClickListener listener) {
		instagram.setOnClickListener(listener);
	}

	public void setWechatListener(View.OnClickListener listener) {
		wechat.setOnClickListener(listener);
	}

	public void setMomentListener(View.OnClickListener listener) {
		wechat_moment.setOnClickListener(listener);
	}

	public void setEmailListener(View.OnClickListener listener) {
		wechat.setOnClickListener(listener);
	}



	public void setCancelListener(View.OnClickListener listener) {
		cancel.setOnClickListener(listener);
	}

//	public void setLeftBtnText(String text){
//		leftBtn.setText(text);
//	}
//
//	public void setRightBtnText(String text){
//		rightBtn.setText(text);
//	}
//
//	/** 确定 */
//	public void setOnPositiveListener(View.OnClickListener listener) {
//		rightBtn.setOnClickListener(listener);
//	}
//
//	/**
//	 * 取消
//	 */
//	public void setOnNegativeListener(View.OnClickListener listener) {
//		leftBtn.setOnClickListener(listener);
//	}
//
//	public void setContent(String content){
//		textContent.setText(content);
//	}



}
