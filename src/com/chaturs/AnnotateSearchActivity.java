package com.chaturs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AnnotateSearchActivity extends Activity implements OnItemClickListener{
	
	 private ListView mList;
	 private TextView mTextView;
	 private List<Annotate> annotationList = new ArrayList<Annotate>();
	 private List<Annotate> searchList = new ArrayList<Annotate>();
	 public static Annotate annotate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setTitle("AnnotationList Search");
		setContentView(R.layout.bookshelf_search);
		annotationList = AnnotateActivity.annotateList ;
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
				 mTextView.setText(getString(R.string.search_results) + "\" " +  query + " \"");
				searchList.clear();
				searchList = getListAnnotationForQuery(query);
				if(searchList.isEmpty()){
					Toast.makeText(this, "Search no results", Toast.LENGTH_SHORT).show();
				
				}
				AnnotationArrayAdapter annotateAdapter = new AnnotationArrayAdapter(this,R.layout.book_display , searchList);
				mList.setAdapter(annotateAdapter);
				mList.setOnItemClickListener(this);
				}
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(!searchList.isEmpty()){
			annotate = searchList.get(arg2);
			Intent intent = new Intent(this, AnnotateSearchDiaplay.class);
			startActivity(intent);
			}
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
		  
		 private List<Annotate> getListAnnotationForQuery(String query){
			 List<Annotate> list = new ArrayList<Annotate>();
			 query = query.toLowerCase().trim();
		Iterator<Annotate> iterator = annotationList.iterator();
		
		while(iterator.hasNext()){
			Annotate annotate = iterator.next();
			if(annotate.getText().toLowerCase().contains(query)){
				list.add(annotate);
			}
			}
			 return list;
		  }
}
