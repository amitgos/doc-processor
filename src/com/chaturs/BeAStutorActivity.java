package com.chaturs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.notepad.NoteEditor;
import com.chaturs.notepad.NotesList;
import com.chaturs.notepad.NotePad.Notes;

public class BeAStutorActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
//	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	      Intent intent = getIntent();
	        if (intent.getData() == null) {
	            intent.setData(Notes.CONTENT_URI);
	        }
	        
	      startActivity(new Intent(Intent.ACTION_INSERT	,getIntent().getData()));
	}
	
	private static final int SETTINGS = Menu.FIRST;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SETTINGS, 0, "Settings")
		  .setIcon(R.drawable.ic_menu_preferences);
		
		return true;
	}
	  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){

		  case SETTINGS:
			  
			  break;
		  }
		  
		return true;
	}
}
