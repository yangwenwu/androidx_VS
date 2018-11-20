package com.lemon.video.update;

public class DataException extends BaseException {
	
	private static final long serialVersionUID = 1L;
	
	public DataException(String msg) {
		super(msg);
		this.printStackTrace();
	}
	
	public DataException(Throwable ex) {
		super(ex);
		this.printStackTrace();
	}
	
	public DataException(String msg,Throwable ex) {
		super(msg,ex);
		this.printStackTrace();
	}
	
	public void printStackTrace() {
		super.printStackTrace();
	}
}
