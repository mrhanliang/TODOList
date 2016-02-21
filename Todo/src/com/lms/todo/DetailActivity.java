package com.lms.todo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lms.todo.db.TableFields;
import com.lms.todo.db.TaskImageLinkManager;
import com.lms.todo.db.TodoTaskManager;
import com.lms.todo.helper.BitmapReader;
import com.lms.todo.helper.Helper;
import com.lms.todo.model.TaskImageLink;
import com.lms.todo.model.TodoTask;

public class DetailActivity extends ActionBarActivity implements OnPageChangeListener{
	
	private static final String TAG = "com.lms.todo.DetailActivity";
	
	public static final String TASK_ID = "TASK_ID";
	
	public static final int REQ_WIDTH = 150;
	
	public static final int REQUEST_FOR_CAMERA = 1;
	public static final int REQUEST_FOR_GALLERY = 2;

	private EditText etTitle, etContent;
	
	private Button btnMarkComplete,btnMarkUnComplete;
	
	private ImageButton ibCamera,ibGallery;
	
	private TodoTaskManager todoTaskManager;
	private TaskImageLinkManager taskImageLinkManager;
	
	private boolean flagForUpdate = false;
	
	private String taskId;
	
	private ActionBar actionBar;
	
	private boolean toMarkComplete = true;
	
	private ViewPager vpGallery;
	private List<String> photoFileNames;
	private List<View> imageViews;
	
	private String tempPhotoFileName;
	
	private boolean isPhotoTaken = false;
	
	private LinearLayout flGalleryContainer;
	private GalleryPagerAdapter galleryPagerAdapter;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_detail);		
		todoTaskManager = new TodoTaskManager(this);
		taskImageLinkManager = new TaskImageLinkManager(this);
		
		photoFileNames = new ArrayList<String>();
		imageViews = new ArrayList<View>();
		
		initComponents();
	}
	
	protected void onDestroy(){
		super.onDestroy();
		todoTaskManager.close();
		taskImageLinkManager.close();
	}
	
	private void initComponents(){
		
		actionBar = getSupportActionBar();		
		actionBar.setIcon(R.drawable.handdraw_notepad);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		etTitle = (EditText)findViewById(R.id.etTitle);
		etContent = (EditText)findViewById(R.id.etContent);
		
		ibCamera = (ImageButton) findViewById(R.id.ibCamera);
		ibCamera.setOnClickListener(cameraListener);
		ibGallery = (ImageButton) findViewById(R.id.ibGallery);
		ibGallery.setOnClickListener(galleryListener);			
		
		taskId = getIntent().getStringExtra(TASK_ID);
		if(taskId != null){
			TodoTask todoTask = todoTaskManager.queryById(taskId);
			if(todoTask != null){
				etTitle.setText(todoTask.getTitle());
				etContent.setText(todoTask.getContent());
				flagForUpdate = true;
				if("N".equals(todoTask.getFlagCompleted())){
					toMarkComplete = true;
				}else{
					toMarkComplete = false;
				}
			}			
			
			List<TaskImageLink> taskImageLinks = taskImageLinkManager.queryByTaskId(taskId);
			Log.v(TAG, "taskid = " + taskId + " Totally " + taskImageLinks.size() + " photos");
			if (!taskImageLinks.isEmpty()) {
				for (int i = 0; i < taskImageLinks.size(); i++) {
					String imagePath = taskImageLinks.get(i).getImagePath();
					Bitmap bitmap = BitmapReader.readBigBitmapFromFile(imagePath, 150);
					imageViews.add(Helper.createImageViewFromBitmap(this,bitmap));
				}
			}
		}			
		
		if(flagForUpdate){
			if(toMarkComplete){
				btnMarkComplete = (Button)findViewById(R.id.btnMarkComplete);
				btnMarkComplete.setVisibility(View.VISIBLE);
				btnMarkComplete.setOnClickListener(markCompleteListener);
			}else{
				btnMarkUnComplete = (Button)findViewById(R.id.btnMarkUnComplete);
				btnMarkUnComplete.setVisibility(View.VISIBLE);
				btnMarkUnComplete.setOnClickListener(markCompleteListener);
			}
		}

		
		vpGallery = (ViewPager)findViewById(R.id.vpGallery);
		galleryPagerAdapter = new GalleryPagerAdapter(imageViews);
		
		vpGallery.setAdapter(galleryPagerAdapter);
		vpGallery.setOnPageChangeListener(this);
		vpGallery.setOffscreenPageLimit(imageViews.size());
		vpGallery.setPageMargin(5);
		if(imageViews.size() > 1){
			vpGallery.setCurrentItem(1);
		}
		
		flGalleryContainer = (LinearLayout)findViewById(R.id.flGalleryContainer);
		flGalleryContainer.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return vpGallery.dispatchTouchEvent(event);
			}
		});
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_save_cancel, menu);
		return true;
	}	
	
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.hold, R.anim.push_right_out);
			break;
		case R.id.action_save:
			save();
			overridePendingTransition(R.anim.hold, R.anim.push_right_out);
			break;
		case R.id.action_cancel:
			cancel();
			overridePendingTransition(R.anim.hold, R.anim.push_right_out);
			break;			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean validation(String title, String content){
		if(title == null || "".equals(title.trim())){
			Toast.makeText(this, "Title is Empty!", Toast.LENGTH_SHORT).show();
			return false;
		}		
		return true;
	}
	
	public void save(){
		String title = etTitle.getText().toString();
		String content = etContent.getText().toString();
		if(validation(title, content)){
			ContentValues contentValues = new ContentValues();
			contentValues.put(TableFields.TodoTask.TITLE, title);
			contentValues.put(TableFields.TodoTask.CONTENT, content);
			contentValues.put(TableFields.TodoTask.FLAG_COMPLETED,"N");
			if(flagForUpdate){
				todoTaskManager.update(contentValues, TableFields.TodoTask.ID + " = ? ", new String[] {taskId});				
			}else{
				long id = todoTaskManager.insert(contentValues);
				taskId = String.valueOf(id);
			}	
			
			if(isPhotoTaken){
				for(int i=0;i<photoFileNames.size();i++){
					String imagePath = photoFileNames.get(i);
					saveImageLink(taskId, imagePath);
				}
			}
			finish();
		}		
	}
	
	private void saveImageLink(String taskId, String imagePath){
		ContentValues imageLinkCV = new ContentValues();
		imageLinkCV.put(TableFields.TaskImageLinkFields.TASK_ID, taskId);
		imageLinkCV.put(TableFields.TaskImageLinkFields.IMAGE_PATH, imagePath);
		taskImageLinkManager.insert(imageLinkCV);
	}
	
	public void cancel(){
		new AlertDialog.Builder(this)
		.setMessage("Input will be lost , sure ? ")
		.setPositiveButton("Sure", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).setNegativeButton("Nope", null).show();
	}
	
	OnClickListener markCompleteListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(flagForUpdate){
				ContentValues contentValues = new ContentValues();
				switch(v.getId()){
				case R.id.btnMarkComplete:
					contentValues.put(TableFields.TodoTask.FLAG_COMPLETED,"Y");
					contentValues.put(TableFields.TodoTask.COMPLETE_TIME, Helper.getDateTime());
					break;
				case R.id.btnMarkUnComplete:
					contentValues.put(TableFields.TodoTask.FLAG_COMPLETED,"N");
					contentValues.put(TableFields.TodoTask.COMPLETE_TIME, "");
					break;														
				}
				todoTaskManager.update(contentValues, TableFields.TodoTask.ID + " = ? ", new String[] {taskId});				
				finish();
			}
		}
	};
	
	OnClickListener cameraListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {			
			startCamera();
		}
	};
	
	OnClickListener galleryListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			startGallery();
		}
	};
	
	private void startCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tempPhotoFileName = Helper.getPhotoFileName();
		File file = new File(tempPhotoFileName);
		if(file.exists()){
			file.delete();
		}
		Uri uri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_FOR_CAMERA);
	}
	
	private void startGallery(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_FOR_GALLERY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){		
		if (requestCode == REQUEST_FOR_CAMERA) {
			if(resultCode == RESULT_OK){
				isPhotoTaken = true;			
				photoFileNames.add(tempPhotoFileName);
				Bitmap bitmap = BitmapReader.readBigBitmapFromFile(tempPhotoFileName,REQ_WIDTH);
				imageViews.add(Helper.createImageViewFromBitmap(this, bitmap));
				refreshGallery();
			}
		} else if (requestCode == REQUEST_FOR_GALLERY) {
			if(resultCode == RESULT_OK){
				isPhotoTaken = true;			
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData();
				tempPhotoFileName = Helper.getImagePath(resolver, uri);
				Log.v(TAG, "Gallery->ImagePath:" + tempPhotoFileName);
				try {
					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, uri);
					Bitmap bitmap = Helper.zoomBitmap(photo, REQ_WIDTH, REQ_WIDTH); 
					
					photoFileNames.add(tempPhotoFileName);
					imageViews.add(Helper.createImageViewFromBitmap(this, bitmap));
					refreshGallery();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void refreshGallery(){
		vpGallery.setOffscreenPageLimit(imageViews.size());
		if(imageViews.size() > 1){
			vpGallery.setCurrentItem(1);
		}		
		galleryPagerAdapter.setListView(imageViews);
		galleryPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if(flGalleryContainer != null){
			flGalleryContainer.invalidate();
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		
	}
}
