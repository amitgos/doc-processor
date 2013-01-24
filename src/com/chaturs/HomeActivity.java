package com.chaturs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chaturs.notepad.CreateNote;
import com.chaturs.notepad.NotePad.Notes;

public class HomeActivity extends Activity {
	
	private ListView list;
	private Dialog dialog;
	private static final String KEY_USERNAME = "key_user";
	private static final String PREFERENCE_USERNAME ="username";
	private static final String KEY_IPVALUE = "key_ipvalue";
	private SharedPreferences sharedPreferences;
	List<String> l= new ArrayList<String>();
	CustomArrayAdapter adapter;
	private Class[] array ={
			
			BookShelfActivity.class,
			StudyGroupActivity.class,
			BigShopSection.class,
			BeAStutorActivity.class,
			NotePaintList.class
		
	};
	
	private static HomeActivity instance;
	
	public static HomeActivity getInstance() {
		return instance;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home);
        
       sharedPreferences =  getSharedPreferences(PREFERENCE_USERNAME, Context.MODE_PRIVATE);
        
        l.add("My BookShelf");
        l.add("Study Group");
        l.add("Big Shop");
        l.add("Be a Stutor");
        l.add("My NoteBook");
        
        list = (ListView) findViewById(R.id.list);
        adapter = new CustomArrayAdapter(this, R.layout.list, l);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				start(arg2);
			}
		});
        
    }
  private void start(int position){
	  Intent i = new Intent(this,array[position]);
	  i.setAction(Intent.ACTION_VIEW);
	  if(position!=3){
	  startActivity(i);
	  }else{
		  Intent intent = getIntent();
	        if (intent.getData() == null) {
	            intent.setData(Notes.CONTENT_URI);
	        }
		  startActivity(new Intent(CreateNote.ACTION_INSERT	,getIntent().getData()));
	  }
  }
    
	private static final int SETTINGS = Menu.FIRST;
	private static final int SEARCH = Menu.FIRST +1;
	
	  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SETTINGS, 0, "Settings")
		  .setIcon(R.drawable.ic_menu_preferences);
		  
		  menu.add(0,SEARCH , 0, "Search")
		  .setIcon(android.R.drawable.ic_search_category_default);
		  
		
		return true;
	}
  
	  @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){
	
		  case SETTINGS:
			  startActivity(new Intent(this, SettingsActivity.class));
			  break;
		  case SEARCH :
			  onSearchRequested();
			  break;
		  }
		return true;
	}
}