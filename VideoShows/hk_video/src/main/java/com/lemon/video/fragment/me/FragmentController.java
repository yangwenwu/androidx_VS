package com.lemon.video.fragment.me;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class FragmentController {

	private int containerId;
	private FragmentManager fm;
	private ArrayList<Fragment> fragments;
	
	private static FragmentController controller;

	//单例
	public static FragmentController getInstance(FragmentActivity activity, int containerId) {
		if (controller == null) {
			controller = new FragmentController(activity, containerId);
		}
		return controller;
	}
	
	public static void onDestroy() {
		controller = null;
	}

	private FragmentController(FragmentActivity activity, int containerId) {
		this.containerId = containerId;
		fm = activity.getSupportFragmentManager();
		initFragment();
	}

	private void initFragment() {
		fragments = new ArrayList<Fragment>();
//		fragments.add(new RegisterFragment());
//		fragments.add(new LoginFragment());
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment : fragments) {
			ft.add(containerId, fragment);
		}
		ft.commit();
	}

	/**
	 * 
	 * 展示指定的某个fragment
	 * @param position
	 */
	public void showFragment(int position) {
		hideFragments();
		Fragment fragment = fragments.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(fragment);
		ft.commit();
	}
	
	/**
	 * 隐藏所有的fragment
	 * 
	 */
	public void hideFragments() {
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment : fragments) {
			if(fragment != null) {
				ft.hide(fragment);
			}
		}
		ft.commit();
	}
	
	/**
	 * 
	 * 获取某一个fragment
	 * @param position
	 * @return
	 */
	public Fragment getFragment(int position) {
		return fragments.get(position);
	}
}