package com.lms.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lms.todo.db.DatabaseManager;

public class AboutActivity extends ActionBarActivity{

	private ActionBar actionBar;
	
	private ListView lvAbout;
	
	private String[] titles = {"Name","Mail","Location","Version","Date"};
	private String[] contents = {"Linmiansheng","sheepjtgjfc@163.com","Guangzhou,GuangDong","0.0.0.1","2014.2"};
	
	private Button btnInitialize;
	
	private DatabaseManager dbManager;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_about);
		
		dbManager = new DatabaseManager(this);
		
		actionBar = getSupportActionBar();		
		actionBar.setIcon(R.drawable.handdraw_notepad);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		initListView();
		btnInitialize = (Button)findViewById(R.id.btnInitialize);
		btnInitialize.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(AboutActivity.this)
				.setTitle("Recover the database ? ")
				.setMessage("Will clear all the tasks ! ")
				.setPositiveButton("Sure", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbManager.recreateTable();							
					}
				}).setNegativeButton("Nope", null)
				.show();				
			}
		});
	}
	
	protected void onPause(){
		super.onPause();
		overridePendingTransition(R.anim.hold, R.anim.push_right_out);
	}
	
	private void initListView(){
		lvAbout = (ListView)findViewById(R.id.lvAbout);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<titles.length;i++){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("title", titles[i]);
			map.put("content", contents[i]);
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, 
				android.R.layout.simple_list_item_2, new String[] {"title","content"}, 
				new int[] {android.R.id.text1, android.R.id.text2});			
		
		lvAbout.setAdapter(simpleAdapter);		
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;		
		}
		return super.onOptionsItemSelected(item);
	}
}
