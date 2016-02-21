package com.lms.todo;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lms.todo.db.TodoTaskManager;
import com.lms.todo.model.TodoTask;

public class MainActivity extends ActionBarActivity {

	private TodoTaskManager todoTaskManager;

	private ListView lvTasks;

	private List<TodoTask> tasks = new ArrayList<TodoTask>();

	private TaskAdapter taskAdapter;
	
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		todoTaskManager = new TodoTaskManager(this);
		tasks = todoTaskManager.queryAll();
		// 2) Initialize the components
		initComponents();

	}

	protected void onResume(){
		super.onResume();
		tasks = todoTaskManager.queryAll();
		taskAdapter.notifyDataSetChanged();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		todoTaskManager.close();
	}

	private void initComponents() {
		actionBar = getSupportActionBar();		
		actionBar.setIcon(R.drawable.handdraw_notepad);				
		actionBar.setHomeButtonEnabled(true);
		
		lvTasks = (ListView) findViewById(R.id.lvTasks);
		taskAdapter = new TaskAdapter();
		lvTasks.setAdapter(taskAdapter);
		lvTasks.setOnItemLongClickListener(onItemLongClickListener);
		lvTasks.setOnItemClickListener(onItemClickListener);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String taskId = String.valueOf(id);
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			intent.putExtra(DetailActivity.TASK_ID, taskId);
			startActivity(intent);
			overridePendingTransition(R.anim.scale_in,R.anim.scale_left_out);
		}
	};
		
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			delete(id);
			return false;
		}
	};
	
	public void delete(final long id){
		new AlertDialog.Builder(this).setTitle("Delete Items")
		.setMessage("Delete ? ")
		.setPositiveButton("Sure", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(todoTaskManager.delete("_id = ? ", new String[] {String.valueOf(id)})){
					tasks = todoTaskManager.queryAll();
					taskAdapter.notifyDataSetChanged();
				}
			}
		}).setNegativeButton("Nop", null).show();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(aboutIntent);
			overridePendingTransition(R.anim.push_right_in,R.anim.hold);
			break;
		case R.id.action_add:
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.scale_in,R.anim.scale_left_out);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class TaskAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public TaskAdapter() {
			layoutInflater = LayoutInflater.from(MainActivity.this);
		}

		@Override
		public int getCount() {
			return tasks.size();
		}

		@Override
		public Object getItem(int position) {
			return tasks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return tasks.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.task_item, null);
				viewHolder.ivComplete = (ImageView)convertView.findViewById(R.id.ivComplete);
				viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}			
			if("Y".equals(tasks.get(position).getFlagCompleted())){
				viewHolder.ivComplete.setVisibility(View.VISIBLE);
			}else{
				viewHolder.ivComplete.setVisibility(View.GONE);
			}
			viewHolder.tvTitle.setText(tasks.get(position).getTitle());
			return convertView;
		}

	}

	static class ViewHolder {
		ImageView ivComplete;
		TextView tvTitle;
	}

}
