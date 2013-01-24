/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chaturs.notepad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;
import com.chaturs.models.StudyGroup;
import com.chaturs.notepad.NotePad.Notes;

/**
 * A generic activity for editing a note in a database.  This can be used
 * either to simply view a note {@link Intent#ACTION_VIEW}, view and edit a note
 * {@link Intent#ACTION_EDIT}, or create a new note {@link Intent#ACTION_INSERT}.  
 */
public class NoteEditor extends Activity {
    
	private static final String TAG = "Notes";

    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] PROJECTION = new String[] {
            Notes._ID, // 0
            Notes.NOTE, // 1
    };
    /** The index of the note column */
    private static final int COLUMN_INDEX_NOTE = 1;
    
    // This is our state data that is stored when freezing.
    private static final String ORIGINAL_CONTENT = "origContent";

    // Identifiers for our menu items.
    private static final int REVERT_ID = Menu.FIRST;
    private static final int DISCARD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    private static final int LIST_ID = Menu.FIRST + 3;
    private static final int SAVE_ID = Menu.FIRST + 4;
    private static final int POST_ID = Menu.FIRST + 5;

    // The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;
    private boolean mNoteOnly = false;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mText;
    private String mOriginalContent;
    private String title;
    private boolean isSaved = false;
    private Dialog dialog = null;
    public static final String ACTION_EDIT = "com.chaturs.notepad.action.EDIT_NOTE";
    private SharedPreferences preferences ;    
    private static final String KEY_USERNAME = "key_user";
	private static final String  DOC_PREFERENCE ="preferences";
	private static final String KEY_IPVALUE = "key_ipvalue";
    private static final int SELECT_STUDY_GROUP = 100;
    private static final int PROGRESS_DIALOG_KEY = 101;
    private List<String> studyGroupNames = new ArrayList<String>();
    private List<StudyGroup> studyGroupList = new ArrayList<StudyGroup>();
    private List<StudyGroup> selectedStudyGroupList = new ArrayList<StudyGroup>();
    private Problem problem;
    private Note note;
    private String IpValue , response;

    public static class LinedEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;

        // we need this constructor for LayoutInflater
        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
           
             mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x800000FF);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            int count = getLineCount();
            Rect r = mRect;
            Paint paint = mPaint;

            for (int i = 0; i < count; i++) {
                int baseline = getLineBounds(i, r);
                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            }
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        final Intent intent = getIntent();
        title = intent.getExtras().getString("note_title");
        // Do some setup based on the action being performed.

        final String action = intent.getAction();
        if (ACTION_EDIT.equals(action)) {
            // Requested to edit: set that state, and the data being edited.
            mState = STATE_EDIT;
            isSaved = false;
            mUri = intent.getData();
        }  else {
            // Whoops, unknown action!  Bail.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        // Set the layout for this activity.  You can find it in res/layout/note_editor.xml
        setContentView(R.layout.note_editor);
       
        preferences = getSharedPreferences(DOC_PREFERENCE ,Context.MODE_PRIVATE);
        IpValue = preferences.getString(KEY_IPVALUE, "");
        // The text view for our note, identified by its ID in the XML file.
        mText = (EditText) findViewById(R.id.note);

        // Get the note!
        mCursor = managedQuery(mUri, PROJECTION, null, null, null);

        // If an instance of this activity had previously stopped, we can
        // get the original text it started with.
        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
        
        DatabaseHandler databasehandler = DatabaseHandler.getInstance();
		studyGroupList = databasehandler.listStudyGroupsInfo();
		Iterator<StudyGroup> iterator = studyGroupList.iterator();
		while(iterator.hasNext()){
			StudyGroup studyGroup = iterator.next();
			studyGroupNames.add(studyGroup.getName());
		}
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCursor != null) {
            mCursor.moveToFirst();
            
            if (mState == STATE_EDIT) {
                setTitle(title);
            } 

            String note = mCursor.getString(COLUMN_INDEX_NOTE);
            mText.setTextKeepState(note);
            
            if (mOriginalContent == null) {
                mOriginalContent = note;
            }

        } else {
            setTitle(getText(R.string.error_title));
            mText.setText(getText(R.string.error_message));
        }
    }

    final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			dismissDialog(PROGRESS_DIALOG_KEY);
			if(response.equals("Success")){
			Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), " Sent failed ", Toast.LENGTH_SHORT).show();
			}
		}

    };
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        // The user is going somewhere else, so make sure their current
//        // changes are safely saved away in the provider.  We don't need
//        // to do this if only editing.
//        if (mCursor != null) {
//            String text = mText.getText().toString();
//            int length = text.length();
//
//            // If this activity is finished, and there is no text, then we
//            // do something a little special: simply delete the note entry.
//            // Note that we do this both for editing and inserting...  it
//            // would be reasonable to only do it when inserting.
//            if (isFinishing() && (length == 0) && !mNoteOnly) {
//                setResult(RESULT_CANCELED);
//                deleteNote();
//
//            // Get out updates into the provider.
//            } else {
//                ContentValues values = new ContentValues();
//
//                // This stuff is only done when working with a full-fledged note.
//                if (!mNoteOnly) {
//                    // Bump the modification time to now.
//                    values.put(Notes.MODIFIED_DATE, System.currentTimeMillis());
//
//                    // If we are creating a new note, then we want to also create
//                    // an initial title for it.
//                    if (mState == STATE_INSERT) {
//                        String title = text.substring(0, Math.min(30, length));
//                        if (length > 30) {
//                            int lastSpace = title.lastIndexOf(' ');
//                            if (lastSpace > 0) {
//                                title = title.substring(0, lastSpace);
//                            }
//                        }
//                        values.put(Notes.TITLE, title);
//                    }
//                }
//
//                // Write our text back into the provider.
//                values.put(Notes.NOTE, text);
//
//                // Commit all of our changes to persistent storage. When the update completes
//                // the content provider will notify the cursor of the change, which will
//                // cause the UI to be updated.
//                getContentResolver().update(mUri, values, null, null);
//            }
//        }
        finish();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Build the menus that are shown when editing.
        if (mState == STATE_EDIT) {
            menu.add(0, REVERT_ID, 0, R.string.menu_revert)
                    .setShortcut('0', 'r')
                    .setIcon(android.R.drawable.ic_menu_revert);
            if (!mNoteOnly) {
                menu.add(0, DELETE_ID, 0, R.string.menu_delete)
                        .setShortcut('1', 'd')
                        .setIcon(android.R.drawable.ic_menu_delete);
                menu.add(0, SAVE_ID, 0, "Save").setIcon(android.R.drawable.ic_menu_save);
                menu.add(0,POST_ID, 0, "Post").setIcon(android.R.drawable.ic_menu_report_image);
            }

        // Build the menus that are shown when inserting.
        } else {
            menu.add(0, DISCARD_ID, 0, R.string.menu_discard)
                    .setShortcut('0', 'd')
                    .setIcon(android.R.drawable.ic_menu_delete);
            menu.add(0,SAVE_ID,0,"Save")
            .setIcon(android.R.drawable.ic_menu_save);
        }

        menu.add(0,LIST_ID,0,"Notes")
        .setIcon(R.drawable.ic_menu_annotatelist);
        
        if (!mNoteOnly) {
            Intent intent = new Intent(null, getIntent().getData());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case DELETE_ID:
            deleteNote();
            finish();
            break;
        case DISCARD_ID:
//            cancelNote();
            break;
        case REVERT_ID:
            cancelNote();
            break;
        case LIST_ID:
        	displayNotes();
        	break;
        case SAVE_ID:
        	
        	showDialogForTitle();
        	break;
        case POST_ID :
        	showDialog(SELECT_STUDY_GROUP);
        	break;
        	
        }
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		
		case SELECT_STUDY_GROUP :
	
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(" Select Study Group(s)");
			final CharSequence [] charArray = new CharSequence[studyGroupNames.size()];

			for(int i=0; i< studyGroupNames.size(); i++) {
				charArray[i] = studyGroupNames.get(i).substring(0);
			}
		
			builder.setMultiChoiceItems(charArray ,null, new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(isChecked){
					selectedStudyGroupList.add(studyGroupList.get(which));
					}else{
						if(!selectedStudyGroupList.isEmpty()){
						selectedStudyGroupList.remove(studyGroupList.get(which));
						}
					}
				}
			});
		
			builder.setPositiveButton("Send",new  DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					if(selectedStudyGroupList.isEmpty()){
						Toast.makeText(getApplicationContext(), 
								"No option selected to send", Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(title == null){
						title = "Sample Note";
					}
					
						DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
				    	String text = mText.getText().toString();
				    	if(text ==null || text.length()== 0){
							text = "empty Note";
						}
				    	
				    	StringBuffer Ids = new StringBuffer();
						for(int i=0;i<selectedStudyGroupList.size();i++){
							if(Ids.length() > 0){
								Ids.append("|");
							}
							long id = selectedStudyGroupList.get(i).getServerId();
							Ids.append(id);
						}
						String user = preferences.getString(KEY_USERNAME, "android");
						note = new Note(Ids.toString(), title, text,user,"");
						selectedStudyGroupList.clear();
						
						dismissDialog(SELECT_STUDY_GROUP);
						showDialog(PROGRESS_DIALOG_KEY);
						
						new Thread(new Runnable() {
							public void run() {
								response = postData();
								handler.sendEmptyMessage(0);
							}
						}).start();
				}
			});	
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			Dialog dialog = builder.create();
			return dialog;
			
		case PROGRESS_DIALOG_KEY:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("sending....");
			progressDialog.setIndeterminate(true);
			return progressDialog;
		
	}
	return null;
	}

    private final void cancelNote() {
        if (mCursor != null) {
            if (mState == STATE_EDIT) {
                // Put the original note text back into the database
                mCursor.close();
                mCursor = null;
                ContentValues values = new ContentValues();
                values.put(Notes.NOTE, mOriginalContent);
                getContentResolver().update(mUri, values, null, null);
            } else if (mState == STATE_INSERT) {
                // We inserted an empty note, make sure to delete it
                deleteNote();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private final void saveNote(){
    	
    	if(mCursor != null){
    		
    		String text = mText.getText().toString();
    		if(text ==null || text.length()== 0){
    			text = "empty Note";
    		}
    		
    		ContentValues values = new ContentValues();
    		  values.put(Notes.NOTE, text);
    		  values.put(Notes.TITLE, title);
    		  values.put(Notes.MODIFIED_DATE, System.currentTimeMillis());
    		  isSaved = true;
              getContentResolver().update(mUri, values, null, null);
    	}
    }

    private final void deleteNote() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mText.setText("");
        }
    }
    
    public String postData() {  
        HttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost(IpValue.trim()+"/docprocessor/addnote.php");  
      
        String title = note.getTitle();
        String content = note.getContent(); 
        String selectedGroupIds = note.getSelectedStudyGroupIds();
        String user = note.getUserName();
//        Toast.makeText(this, "Selected GroupIds  " + selectedGroupIds, Toast.LENGTH_SHORT).show();
        long study_group_id = note.getStudygroupId();
        if(title == null){
        	title = "sample";
        }
        if(content == null){
        	content = "No Content";
        }
        
        try {    
           
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
            nameValuePairs.add(new BasicNameValuePair("title", title));  
            nameValuePairs.add(new BasicNameValuePair("content", content));
            nameValuePairs.add(new BasicNameValuePair("groups", selectedGroupIds));
            nameValuePairs.add(new BasicNameValuePair("user", user));
           httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
           ResponseHandler<String> responseHandler = new BasicResponseHandler();

           String response = httpclient.execute(httppost, responseHandler);
           Log.i("test", ""+ response);
           return response;
//            Toast.makeText(this, ""+response, Toast.LENGTH_SHORT) ;
        } catch (ClientProtocolException e) { 
        	Log.e("test", ""+ e.getMessage());
        	return e.getMessage();
        } catch (IOException e) { 
        	Log.e("test", ""+ e.getMessage());
        	return e.getMessage();
        }  
    }

    private final void displayNotes(){
    	
    	startActivity(new Intent(getApplicationContext(),NotesList.class));
    }
    
    private void showDialogForTitle(){
    	
  		View view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null);
  		final EditText edit = (EditText) view.findViewById(R.id.edit);
  		Button okButton = (Button) view.findViewById(R.id.ok);
  		Button cancelButton = (Button) view.findViewById(R.id.cancel);
  		
  		edit.setText(title);
  		AlertDialog.Builder builder = new AlertDialog.Builder(this);
  		builder.setTitle("Enter Title");
  		builder.setView(view);
  		builder.setCancelable(false);
  		
  		okButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				title = edit.getText().toString();

  				if(title.trim().length() == 0){
  					edit.setError("Please Enter Some Text !");
  					 return;
  				}
  				
  				saveNote();
  				setTitle(title);
  				dialog.dismiss();
			}
		});
  		
  		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
			dialog.dismiss();
				
			}
		});
  	
  		 dialog = builder.create();
  		 dialog.show();
    }
}
