package com.lms.todo.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapReader {

	public static Bitmap readBitmapFromResource(Context context, int resId){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inPreferredConfig = Bitmap.Config.ARGB_8888;
		op.inDither = false;
		op.inScaled = false;
		
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is,null,op);
	}
	
	/**
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap readBitmapFromFile(String imagePath){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inPreferredConfig = Bitmap.Config.ARGB_8888;
		op.inDither = false;
		op.inScaled = false;
		
		return BitmapFactory.decodeFile(imagePath, op);
	}
	
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth){
		final int width = options.outWidth;
		int inSampleSize = 1;
		if(width > reqWidth){
			final int widthRatio = Math.round((float)width / (float)reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}
	
	public static Bitmap readBigBitmapFromFile(String imagePath, int reqWidth){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath,options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		options.inScaled = false;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imagePath, options);
	}
	
	/**
	 * 
	 * @param imgUrl
	 * @return
	 */
	public static Bitmap readBitmapFromUrl(String imgUrl){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inPreferredConfig = Bitmap.Config.ARGB_8888;
		op.inDither = false;
		op.inScaled = false;
		Bitmap bitmap = null;
		try {
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6 * 1000);
			if(conn.getResponseCode() != 200) throw new RuntimeException("Request Failed");
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is,null,op);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return bitmap;
	}
}
