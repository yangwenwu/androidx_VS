package com.lemon.video.fragment.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lemon.video.R;
import com.lemon.video.base.activity.BaseAppActivity;
import com.lemon.video.utils.NetWorkUtil;
import com.lemon.video.utils.ToastUtils;


public class WebActivity extends BaseAppActivity implements View.OnClickListener{

	protected WebView webview;
	private LinearLayout lineProgress;
	private ImageView back_web;
	private TextView titleName;
	private ImageView share;
    private String img;
    private String url;
    private String title;
	private String append;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		lineProgress = (LinearLayout)findViewById(R.id.lineprogress);
		titleName= (TextView) findViewById(R.id.titleName);
		webview = (WebView)this.findViewById(R.id.webv);
		back_web = (ImageView) findViewById(R.id.back_web);
		share = (ImageView) findViewById(R.id.share);
		back_web.setOnClickListener(this);
		share.setOnClickListener(this);

		webview.setWebChromeClient(new MyWebChromeClient());
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setBlockNetworkImage(false); // 解决图片不显示
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
			webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		Intent intent = this.getIntent();
		//设置标题
		title = intent.getStringExtra("titleName");
		titleName.setText(title);
		url = intent.getStringExtra("url");
//		url ="http://zhtbapp.hizh.cn/news/?from=singlemessage&isappinstalled=1#/liveMobileList/6309514eM8870M480dMa07cMaaabb349b35e/9cca30f9M53d7M42dbMb205Mb3f79a81dfed?showFlag=1";

        img = intent.getStringExtra("img");
		if(TextUtils.isEmpty(url)){
			String type = intent.getStringExtra("type");
			if(type != null && type.equals("content")){
				String data = intent.getStringExtra("htmldata");
				webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
			}else{
				webview.loadDataWithBaseURL(null, "暂无数据", "text/html", "utf-8", null);
			}
		}else{
			webview.loadUrl(url);
		}
		
		webview.setWebViewClient(new MyWebViewClient());

	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(lineProgress.getVisibility() == View.VISIBLE)
//		{
//			lineProgress.setVisibility(View.GONE);
//			return true;
//		}
//	    else if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack())
//		{
//			webview.goBack();
//			return true;
//		}
//		else if(keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			finish();
//			return true;
//		}
//		else
//		{
//			return super.onKeyDown(keyCode, event);
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();
		if (webview != null) {
			webview.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (webview != null) {
			webview.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webview != null) {
			webview.destroy();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.share:
				if (!NetWorkUtil.isNetworkAvailable(WebActivity.this)){
					ToastUtils.showShort(WebActivity.this,getResources().getString(R.string.load_fail));
					return;
				}

				break;
			case R.id.back_web:
				finish();
				break;
		}
	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MyWebViewClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.endsWith("@chinadailyhk.com")){

			}else{
				view.loadUrl(url);
			}
			return true;
		}
    	
    }
    
    private class MyWebChromeClient extends WebChromeClient
    {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(newProgress > 0 && newProgress < 40)
			{
				lineProgress.setVisibility(View.VISIBLE);
			}
			else 
			{
				lineProgress.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}



	}


	public static void openWebWindow(Context context, String type ){
    	openWebWindow(context,type,null);
    }
    
    public static void openWebWindow(Context context, String type,String titlename){
    	Intent intent = new Intent(context, WebActivity.class);
		intent.putExtra("type", type);
		
		if(titlename != null && titlename.length() > 0)
				intent.putExtra("titleName", titlename);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		context.startActivity(intent);
    }
    
    public static void openWebWindowByContent(Context context, String content, String titleName , String img){
    	Intent intent = new Intent(context, WebActivity.class);
		intent.putExtra("url", content);
		intent.putExtra("htmldata", content);
		intent.putExtra("titleName",titleName) ;
		intent.putExtra("img",img);

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		context.startActivity(intent);
    }
    
    }
    
