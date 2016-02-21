package com.lms.todo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lms.todo.model.TaskImageLink;


public class TaskImageLinkManager extends DatabaseManager{

	private static final String TAG = "com.lms.todo.db.TaskImageLinkManager";
	
	public TaskImageLinkManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public long insert(ContentValues contentValues){
		return super.insert(TableFields.TABLE_IMAGE_LINK, contentValues);
	}
	
	public long update(ContentValues contentValues, String whereClause, String[] whereArgs){
		return super.update(TableFields.TABLE_IMAGE_LINK, contentValues, whereClause, whereArgs);
	}
	
	public boolean delete(String whereClause, String[] whereArgs){		
		return super.delete(TableFields.TABLE_IMAGE_LINK, whereClause, whereArgs);		
	}


	public List<TaskImageLink> queryByTaskId(String taskId){
		List<TaskImageLink> list = new ArrayList<TaskImageLink>();		
		Cursor cursor = database.rawQuery(TableFields.TaskImageLinkFields.SQL_QUERY_BY_TASK_ID, new String[] {taskId});
		if(cursor == null){
			
		}else if(!cursor.moveToFirst()){
			
		}else{
			int columnId = cursor.getColumnIndex(TableFields.TaskImageLinkFields.ID);
			int columnImagePath = cursor.getColumnIndex(TableFields.TaskImageLinkFields.IMAGE_PATH);			
			
			do{
				int id = cursor.getInt(columnId);
				String imagePath = cursor.getString(columnImagePath);
				
				TaskImageLink taskImageLink = new TaskImageLink();
				taskImageLink.setId(id);
				taskImageLink.setImagePath(imagePath); 
				
				list.add(taskImageLink);
				
			}while(cursor.moveToNext());
		}
		cursor.close();
		Log.v(TAG, "Total Records : " + list.size());
		return list;
	}
	
}
