package com.lemon.video.update;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lemon.video.R;


/**
 * 自定义dialog
 */
public class UpdateDialog extends Dialog  {
	private Button leftBtn;
	private Button rightBtn;
    private TextView textContent;

	public UpdateDialog(Context context) {
		super(context, R.style.DialogWithDim);
		setDialog();
	}

	private void setDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
		leftBtn = (Button) mView.findViewById(R.id.left_btn);
		rightBtn = (Button) mView.findViewById(R.id.right_btn);
		textContent = (TextView) mView.findViewById(R.id.content_text);
		this.setCanceledOnTouchOutside(false);
		this.setCancelable(false);
		super.setContentView(mView);

		Window dialogWindow = getWindow();
		WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics size = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(size);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (size.widthPixels * 0.9); // 高度设置为屏幕的0.9
//		p.height = (int) (size.heightPixels * 0.7); //高度设置为屏幕的0.7
		dialogWindow.setAttributes(p);
	}

	public void setLeftBtnText(String text){
		leftBtn.setText(text);
	}

	public void setRightBtnText(String text){
		rightBtn.setText(text);
	}

	/** 确定 */
	public void setOnPositiveListener(View.OnClickListener listener) {
		rightBtn.setOnClickListener(listener);
	}

	/**
	 * 取消
	 */
	public void setOnNegativeListener(View.OnClickListener listener) {
		leftBtn.setOnClickListener(listener);
	}

	public void setContent(String content){
		textContent.setText(content);
	}



}
