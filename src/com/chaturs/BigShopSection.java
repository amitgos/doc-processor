package com.chaturs;

import java.util.ArrayList;
import java.util.List;
import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.notepad.NotePad.Notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BigShopSection extends Activity {
	
	private List<String> l= new ArrayList<String>();
	private BigShopSectionAdapter adapter;
	private Class[] array ={
			
		
	};
	private ListView list;
	
	private static BigShopSection instance;
	
	public static BigShopSection getInstance() {
		return instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 instance = this;
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
//	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.bigshop_section);
	        
	        l.add("Free");
	        l.add("New");
	        l.add("Most Popular");
	        l.add("Suggested For U");
	        
	        list = (ListView) findViewById(R.id.list);
	        adapter = new BigShopSectionAdapter(this, R.layout.list, l);
	        list.setAdapter(adapter);
	        list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					start(arg2);
				}
			});
	}
	
	private void start(int position){
		  Intent i = new Intent(this,BigShopActivity.class);
		  startActivity(i);
	  }

	private static final int SETTINGS = Menu.FIRST;
	private static final int SEARCH = Menu.FIRST +1;
	
	  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SETTINGS, 0, "Settings")
		  .setIcon(android.R.drawable.ic_menu_preferences);
		  
		  menu.add(0, SEARCH, 0, "Search")
		  .setIcon(android.R.drawable.ic_search_category_default);
		
		return true;
	}
	  
	  @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){

		  case SETTINGS:
			  
			  break;
			
		  case SEARCH:
			  onSearchRequested();
			  break;
		  
		  }
		return true;
	}
	  
}
