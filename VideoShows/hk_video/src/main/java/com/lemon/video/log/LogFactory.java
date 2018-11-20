package com.lemon.video.log;


import android.text.TextUtils;

public class LogFactory {
	private static final String TAG = "CommonLog";
	private static CommonLog log = null;

	public static CommonLog createLog() {
		if (log == null) {
    		log = new CommonLog();
		}

		log.setTag(TAG);
		return log;

	}

	public static CommonLog createLog(String tag) {
		if (log == null) {
			log = new CommonLog();
		}
		
		if (TextUtils.isEmpty(tag)) {
    		log.setTag(TAG);
		} else {
			log.setTag(tag);
		}
		return log;
	}
	
	public void release(){
		log = null;
	} 
}