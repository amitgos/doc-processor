package com.chaturs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geometerplus.android.fbreader.MyBookreaderActivity;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chaturs.models.Book;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;
import com.chaturs.models.SearchObject;

public class SearchActivity extends Activity implements OnItemClickListener{
	
	private String action;
	 private ListView mList;
	 private TextView mTextView;
	 private List<Book> resultBooks = new ArrayList<Book>();
	 private List<Problem> problems = new ArrayList<Problem>();
	 private List<Note> notes = new ArrayList<Note>();
	 private List<SearchObject> objects =new ArrayList<SearchObject>();
	 DatabaseHandler database ;
	 private static final String BOOKS_PATH = Environment
		.getExternalStorageDirectory()
		+ "/Books/DocProcessor/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("App Search");
		setContentView(R.layout.bookshelf_search);
		database = DatabaseHandler.getInstance();
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
			
			
			resultBooks.clear();
			resultBooks = database.bookListForQuery(query);
			problems = database.problemListForquery(query);
			notes = database.noteListForquery(query);
			objects = getSearchObjects();
			if(objects.isEmpty()){
				
				Toast.makeText(this, "search no results", Toast.LENGTH_SHORT).show();
				
			}
			HomeSearchAdapter homesearchAdapter = new HomeSearchAdapter(this,R.layout.home_search_display , objects);
			mList.setAdapter(homesearchAdapter);
			mList.setOnItemClickListener(this);
			}
	}
	
	private List<SearchObject> getSearchObjects(){
		
		List<SearchObject> listSearchObjects = new ArrayList<SearchObject>();
		if(!resultBooks.isEmpty()){
			for(int i=0; i<resultBooks.size();i++){
				String title = resultBooks.get(i).getTitle();
				String subTitle = resultBooks.get(i).getAuthor();
				String objectType = "Book";
				Book book = resultBooks.get(i);
				listSearchObjects.add(new SearchObject(title, subTitle,"", objectType, book, null,null));
			}
		}
		
		if(!problems.isEmpty()){
			
			for(int i=0; i<problems.size();i++){
				Problem problem = problems.get(i);
				String title = problem.getName();
				String subTitle = database.getStudyGroupNameFromServerId(problem.getStudygroupServerId());
				String objectType = "Problem";
				String user = problem.getUserName();
				listSearchObjects.add(new SearchObject(title, subTitle,user, objectType, null , problem,null));
			}
		}
		
		
		if(!notes.isEmpty()){
			
			for(int i=0; i<notes.size();i++){
				Note note = notes.get(i);
				String title = note.getTitle();
				 String subTitle = database.getStudyGroupNameFromServerId(note.getStudygroupId());
				String user = note.getUserName();
				String objectType = "Note";
				
				listSearchObjects.add(new SearchObject(title, subTitle,user, objectType, null , null,note));
			}
			
		}
		Comparator<SearchObject> comparator = new Comparator<SearchObject>() {

			public int compare(SearchObject object1, SearchObject object2) {
				
				return object1.getTitle().compareToIgnoreCase(object2.getTitle());
			}
		};
		Collections.sort(listSearchObjects, comparator);
		return listSearchObjects;
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
		if("Book".equals(objects.get(arg2).getObjectType())){
			
		String path = objects.get(arg2).getmBook().getFilePath();
//		Uri.Builder uriBuilder = new Uri.Builder();
//		uriBuilder.path(path);
//		Uri uri =uriBuilder.build();
		
		Intent intent = new Intent(this,MyBookreaderActivity.class);
//		intent.setData(uri);
//		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra("book_path", path);
		startActivity(intent);
		
		}else if("Problem".equals(objects.get(arg2).getObjectType())){
			
			Intent intent = new Intent(this, StudyGroupProblemActivity.class);
			intent.putExtra("problem_server_id",objects.get(arg2).getmProblem().getServerId());
			startActivity(intent);
		}else if("Note".equals(objects.get(arg2).getObjectType())){
			
			Note note = objects.get(arg2).getNote();
			String title = note.getTitle();
			String content = note.getContent();
			 Intent intent = new Intent(this, NoteDisplayActivity.class);
			 intent.putExtra("note_title", title);
			 intent.putExtra("note_content",content);
			 startActivity(intent);
		}
	}
	
	private void Sort(){
		
		Comparator<SearchObject> comparator = new Comparator<SearchObject>() {

			public int compare(SearchObject object1, SearchObject object2) {
				
				return object1.getTitle().compareToIgnoreCase(object2.getTitle());
			}
		};
		Collections.sort(objects, comparator);
	}
	
	private static final int SEARCH = Menu.FIRST +1;
	
	  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SEARCH, 0, "Search")
		  .setIcon(R.drawable.ic_menu_search);
		
		return true;
	}
	  
	  @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){

		  case SEARCH:
			  onSearchRequested();
			  break;
		  }
		return true;
	}
	
}
