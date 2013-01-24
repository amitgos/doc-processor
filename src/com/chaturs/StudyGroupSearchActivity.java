package com.chaturs;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;

public class StudyGroupSearchActivity extends Activity implements OnItemClickListener{

		private String action;
		private ListView mList;
		private TextView mTextView;
		private List<Problem> problems = new ArrayList<Problem >();
		private List<Note> notes = new ArrayList<Note>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("StudyGroupSearch");
		setContentView(R.layout.bookshelf_search);
		
		mList = (ListView) findViewById(R.id.list);
		mTextView = (TextView) findViewById(R.id.textField);
		
		Intent intent = getIntent();
		goSearch(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
		setIntent(intent);
		goSearch(intent);
		
	}
	
	private void goSearch(Intent intent){
		
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			
			String query = intent.getStringExtra(SearchManager.QUERY);
			getString(R.string.search_results);
			mTextView.setText(getString(R.string.search_results) + "  \" " +  query + " \"");
			DatabaseHandler database = DatabaseHandler.getInstance();
			problems.clear();
			problems = database.problemListForquery(query);
			notes = database.noteListForquery(query);
			if(problems.isEmpty() && notes.isEmpty()){
			Toast.makeText(this, "search no results", Toast.LENGTH_SHORT).show();
			}
			
			StudyGroupSearchAdapter studyGroupSearchAdapter = 
				new StudyGroupSearchAdapter(this, R.layout.problem_search_display, problems,notes);
			mList.setAdapter(studyGroupSearchAdapter);
			mList.setOnItemClickListener(this);
		}
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 ;
		int size = problems.size();
		if(position < size ){
			Intent intent = new Intent(this, StudyGroupProblemActivity.class);
			Log.i("PROBLEM ID", "" + problems.get(arg2).getServerId());
			intent.putExtra("problem_server_id",problems.get(position).getServerId());
			startActivity(intent);
			
		}else{
			String title = notes.get(Math.abs(size - position)).getTitle();
			String note = notes.get(Math.abs(size - position)).getContent();
			 Intent intent = new Intent(this, NoteDisplayActivity.class);
			 intent.putExtra("note_title", title);
			 intent.putExtra("note_content", note);
			 startActivity(intent);
		}
	}
	
	private static final int SETTINGS = Menu.FIRST;
	private static final int SEARCH = Menu.FIRST +1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SEARCH ,0, "Search")
		  .setIcon(android.R.drawable.ic_search_category_default);
		
		return true;
	}
	  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){
		 			  
		  case SEARCH :
			  onSearchRequested();
			  return true;
		  }
		return super.onOptionsItemSelected(item);
	}
}
