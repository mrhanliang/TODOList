package com.lms.todo;


import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GalleryPagerAdapter extends PagerAdapter{

	private List<View> mViews;
	private int size;
	
	public GalleryPagerAdapter(List<View> views){
		mViews = views;
		size = views.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		position = position % size;
		View view = mViews.get(position);
		container.addView(view);
		return view;
	}
	
	public void setListView(List<View> views){
		mViews = views;
		size = views.size();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		View view = (View)object;
		container.removeView(view);
		view = null;
	}
}
