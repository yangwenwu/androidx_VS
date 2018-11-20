package com.lemon.video.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lemon.video.R;


/**
 * @author k.yang
 * @version 创建时间：2015-8-26 下午3:56:55 类说明
 */
public class ChoseImageDialog extends Dialog{
	private TextView select_camera,select_photo,select_cancel;

//	private ImageView image;
	public ChoseImageDialog(Context context) {
		super(context, R.style.DialogWithDim);
		setDialog();
}

	private void setDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(R.layout.chose_image_dialog, null);
		select_camera = (TextView) mView.findViewById(R.id.select_camera);
		select_photo = (TextView) mView.findViewById(R.id.select_photo);
		select_cancel = (TextView) mView.findViewById(R.id.select_cancel);
//		image = (ImageView) mView.findViewById(R.id.image);
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		super.setContentView(mView);

		Window dialogWindow = getWindow();
//		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics size = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(size);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (size.widthPixels * 0.9); // 高度设置为屏幕的0.9
//		p.height = (int) (size.heightPixels * 0.7); //高度设置为屏幕的0.7
		dialogWindow.setAttributes(p);

	}

	/** camera */
	public void choseCamera(View.OnClickListener listener) {
		select_camera.setOnClickListener(listener);
	}

	/**
	 * photo
	 */
	public void chosePhoto(View.OnClickListener listener) {
		select_photo.setOnClickListener(listener);
	}

	/**
	 * cancel
     */
	public void choseCancle(View.OnClickListener listener) {
		select_cancel.setOnClickListener(listener);
	}

//	public void setImage(Drawable drawable) {
//		image.setImageDrawable(drawable);
//	}
//
//	public void setImage(Bitmap bitmap){
//		image.setImageBitmap(bitmap);
//	}



}
