package com.chaturs;

import java.net.URI;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.notepad.NotesList;
import com.chaturs.provider.NotePad.Notes;

public class NoteDisplayActivity extends Activity{

	private TextView mText;
	private String title;
	private String note;
	private static final int MENU_SAVE = Menu.FIRST;
	private static final int MENU_NOTES = Menu.FIRST + 1;
	private Uri mUri;
	private static boolean sSave = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.note_editor);
		mText = (EditText) findViewById(R.id.note);
		Bundle bundle =getIntent().getExtras();
		if(bundle != null){
			
			title = bundle.getString("note_title");
			note = bundle.getString("note_content");
			
		}
		
		if(title != null){
			setTitle(title);
		}
		if(note != null){
			mText.setText(note);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SAVE,0, "Save");
		menu.add(0, MENU_NOTES, 0, "My Notes");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case MENU_SAVE:
				saveNote();
			break;
			
		case MENU_NOTES :
			startActivity(new Intent(this, NotesList.class));
			 break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void saveNote(){
		ContentValues values = new ContentValues();
		if(mUri == null){
		
		values.put(Notes.TITLE, title); 
		values.put(Notes.NOTE, note);
		mUri = getContentResolver().insert(com.chaturs.notepad.NotePad.Notes.CONTENT_URI, values);
		sSave = true;
		}else{
			values.put(Notes.TITLE, title); 
			values.put(Notes.NOTE, note);
			 getContentResolver().update(mUri, values, null, null);
		}
	}
}
