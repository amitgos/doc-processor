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
import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;
import com.chaturs.models.StudyGroup;
import com.chaturs.notepad.NotePad.Notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNote extends Activity{

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
	    private Dialog dialog = null;
	    private String title;
	    private boolean isSaved = false;
	    private boolean mNotes = false;
	    public static final String ACTION_INSERT = "com.chaturs.notepad.action.INSERT_NOTE";
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
	    private ProgressDialog progressDialog;
	    private Thread thread;
	    private Note note;
	   private static String IpValue,response;
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
	        
//	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        Log.i("Cur Activity","On Create");
	        final Intent intent = getIntent();

	        // Do some setup based on the action being performed.

	        final String action = intent.getAction();
	       if (ACTION_INSERT.equals(action)) {
	            // Requested to insert: set that state, and create a new entry
	            // in the container.
	            mState = STATE_INSERT;
	            setTitle(getText(R.string.title_create));
	            mUri = getContentResolver().insert(intent.getData(), null);

	            // If we were unable to create a new note, then just finish
	            // this activity.  A RESULT_CANCELED will be sent back to the
	            // original activity if they requested a result.
	            if (mUri == null) {
	                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
	                finish();
	                return;
	            }

	            // The new entry was created, so assume all will end well and
	            // set the result to be returned.
	            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

	        } else {
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

	  Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissDialog(PROGRESS_DIALOG_KEY);
			if(response.equals("Success")){
			Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "Sent failed", Toast.LENGTH_SHORT).show();
			}
		}
	  };
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        Log.i("Cur Activity","On Resume");
	        if (mCursor != null) {
	            mCursor.moveToFirst();

	            if(title != null){
	                setTitle(title + "(New)");
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

	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        // Save away the original text, so we still have it if the activity
	        // needs to be killed while paused.
	    	Log.i("cur Activity","ONSAVE INSTANCE");
	        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
	       
	    }
		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			 Log.i("cur Activity","ONRestore INSTANCE");
		}
	    @Override
	    protected void onPause() {
	        super.onPause();
	    
	        Log.i("Cur Activity","On Pause");
	        if ( mCursor != null && !isFinishing() ) {
	        	 Log.i("Cur Activity","On Pause In LOOP");
	            String text = mText.getText().toString();
	           	            
	            	int length = text.length();
	           
	                ContentValues values = new ContentValues();

	                    if (mState == STATE_INSERT && isSaved) {
	                    	if(title == null || title.length()== 0){
	                        title = text.substring(0, Math.min(20, length));
	                        if (length > 20) {
	                            int lastSpace = title.lastIndexOf(' ');
	                            if (lastSpace > 0) {
	                                title = title.substring(0, lastSpace);
	                            }
	                        }
	                    	}
//	                        values.put(Notes.TITLE, title);
	                    }
	                values.put(Notes.NOTE, text);
	                getContentResolver().update(mUri, values, null, null);
	        }
	    }
	    
		@Override
		protected void onStop() {
			super.onStop();
			
			Log.i("Cur Activity","On Stop");
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			
			Log.i("Cur Activity","On Destroy");
			if(!isSaved){
	        	
	        	if (mCursor != null) {
		            mCursor.close();
		            mCursor = null;
		            getContentResolver().delete(mUri, null, null);
		            mText.setText("");
		        }
			}
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
							Log.i("Test", which + "Checked");
						selectedStudyGroupList.add(studyGroupList.get(which));
						}else{
							Log.i("Test", which + "UnChecked");
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
						
						if(title == null || title.length() == 0){
							title = "sample Note";
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
							 dialog.dismiss();
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
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("sending....");
				progressDialog.setIndeterminate(true);
				return progressDialog;
		}
		return null;
		}

		@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);

	        // Build the menus that are shown when editing.
	        if (mState == STATE_INSERT) {
	            menu.add(0, DISCARD_ID, 0, R.string.menu_discard)
	                    .setShortcut('0', 'd')
	                    .setIcon(android.R.drawable.ic_menu_delete);
	        menu.add(0,LIST_ID,0,"Notes")
	        .setIcon(R.drawable.ic_menu_annotatelist);
	        menu.add(0,SAVE_ID,0,"Save")
	        .setIcon(android.R.drawable.ic_menu_save);
	        
	        menu.add(0, POST_ID, 0, "Post").setIcon(android.R.drawable.ic_menu_report_image);
	        }
	     
	        if (!mNoteOnly) {
	            Intent intent = new Intent(null, getIntent().getData());
	            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
	            menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
	                    new ComponentName(this, NoteEditor.class), null, intent, 0, null);
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
	            cancelNote();
	            break;
	        case REVERT_ID:
	            cancelNote();
	            break;
	        case LIST_ID:
	        	mNotes = true;
	        	displayNotes();
	        	break;
	        case SAVE_ID:
	        	showDialogForTitle();
	        	
	        	break;
	        case POST_ID:
	        	showDialog(SELECT_STUDY_GROUP);
	        	
	        	break;
	        }
	        return super.onOptionsItemSelected(item);
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
	    		        
	        if (mCursor != null) {
	        	
	            String text = mText.getText().toString();
	            int length = text.length();

	            
	            if ((length == 0) && !mNoteOnly) {
	            	text= "empty note";
	            } 
	            
	               ContentValues values = new ContentValues();

	                    values.put(Notes.MODIFIED_DATE, System.currentTimeMillis());
	                     values.put(Notes.TITLE, title);
	                values.put(Notes.NOTE, text);
	                getContentResolver().update(mUri, values, null, null);
	        }
	    }
	        
	    public String postData() {  
	    	dismissDialog(SELECT_STUDY_GROUP);
	    	
	        HttpClient httpclient = new DefaultHttpClient();  
	        HttpPost httppost = new HttpPost(IpValue + "/docprocessor/addnote.php");  
	      
	        String title = note.getTitle();
	        String content = note.getContent(); 
	        String selectedGroupIds = note.getSelectedStudyGroupIds();
	        String user = note.getUserName();
//	        Toast.makeText(this, "Selected GroupIds  " + selectedGroupIds, Toast.LENGTH_SHORT).show();
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
	        } catch (ClientProtocolException e) { 
	        	Log.e("test", ""+ e.getMessage());
	        	return e.getMessage();
	        } catch (IOException e) { 
	        	Log.e("test", ""+ e.getMessage());
	        	return e.getMessage();
	        }  
	    }
	    
	    private void showDialogForTitle(){
	    	
	  		View view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null);
	  		final EditText edit = (EditText) view.findViewById(R.id.edit);
	  		Button okButton = (Button) view.findViewById(R.id.ok);
	  		Button cancelButton  = (Button) view.findViewById(R.id.cancel);
	  		
	  		if(isSaved && title != null){
	  			edit.setText(title);
	  		}
	  		
	  		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  		builder.setTitle("Enter Title");
	  		builder.setView(view);
	  		builder.setCancelable(false);
	  		okButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					
					title = edit.getText().toString();
					
					int len = title.length();

	  				if(title.trim().length() == 0){
	  					edit.setError("Please Enter Some Text !");
	  					 return;
	  				}
	  				isSaved = true;
	  				saveNote();
	  				setTitle(title + "(New)");
					
	  				dialog.dismiss();
				}
			});
	  		
	  		cancelButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
	  		
	  		 dialog = builder.create();
	  		 dialog.setCancelable(false);
	  		 dialog.show();
	    }
	  
	    private final void deleteNote() {
	        if (mCursor != null) {
	            mCursor.close();
	            mCursor = null;
	            getContentResolver().delete(mUri, null, null);
	            mText.setText("");
	        }
	    }
	    
	    private final void displayNotes(){
	    	startActivity(new Intent(getApplicationContext(),NotesList.class));
	    }
}
