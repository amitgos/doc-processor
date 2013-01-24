package com.chaturs;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.MyBookreaderActivity;
import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Book;
import com.chaturs.models.DatabaseHandler;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BookShelfSearchActivity extends Activity implements AdapterView.OnItemClickListener{
	
	private String action;
	 private ListView mList;
	 private TextView mTextView;
	 private List<Book> resultBooks = new ArrayList<Book>();
	 private static final String BOOKS_PATH = Environment
		.getExternalStorageDirectory()
		+ "/Books/DocProcessor/";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("BookShelfSearch");
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
			resultBooks.clear();
			resultBooks = database.bookListForQuery(query);
			if(resultBooks.isEmpty()){
			Toast.makeText(this, "search no results", Toast.LENGTH_SHORT).show();
			}
			BookShelfArrayAdapter bookShelfAdapter = new BookShelfArrayAdapter(this,R.layout.book_display , resultBooks);
			mList.setAdapter(bookShelfAdapter);
			mList.setOnItemClickListener(this);
			}
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		

		String path = resultBooks.get(arg2).getFilePath();
//		Uri.Builder uriBuilder = new Uri.Builder();
//		uriBuilder.path(path);
//		Uri uri =uriBuilder.build();
		
		Intent intent = new Intent(this,MyBookreaderActivity.class);
		intent.putExtra("book_path", path);
//		intent.setData(uri);
//		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
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
