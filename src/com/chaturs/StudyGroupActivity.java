package com.chaturs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.geometerplus.zlibrary.ui.android.R;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.StudyGroup;

public class StudyGroupActivity extends Activity {

	private static Gallery gallery;
	//private List<String> StudyGroups = new ArrayList<String>();
	//private List<String> problemList = new ArrayList<String>();
	private List<StudyGroup> studyGroups;
	private static final int SEND = 100;
	private ProgressDialog progressDialog;
	private static final int PROGRESS_BAR = 101;
	private DatabaseHandler database;
	private static final String KEY_USERNAME = "key_user";
	private static final String  DOC_PREFERENCE ="preferences";
	private static final String KEY_IPVALUE = "key_ipvalue";
	private SharedPreferences preferences;
	private String IpValue , response;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
//	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		 setContentView(R.layout.study_group);
		 
		preferences = getSharedPreferences(DOC_PREFERENCE, Context.MODE_PRIVATE);
		IpValue = preferences.getString(KEY_IPVALUE, "");
		
		database = DatabaseHandler.getInstance();
	    studyGroups = database.listStudyGroups();
		gallery = (Gallery) findViewById(R.id.gallery_study_group);
		gallery.setAdapter(new StudyGroupAdapter(getApplicationContext()));
		
		showDialog(PROGRESS_BAR);
		
		new Thread(new Runnable() {
			
			public void run() {
				
				response = UpdateStudyGroupListFromServer();
				handler.sendEmptyMessage(0);
				
			}
		}).start();
	}
	
	final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			dismissDialog(PROGRESS_BAR);
			 studyGroups = database.listStudyGroups();
			gallery.setAdapter(new StudyGroupAdapter(getApplicationContext()));
			if(response != "Success"){
			Toast.makeText(getApplicationContext(), "Sync failed", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public static Gallery  getGallery(){
		
		return gallery;
	}
	
	public class StudyGroupAdapter extends BaseAdapter{

		Context mContext;
		int curPos;
		
		public StudyGroupAdapter(Context context){
			
			mContext = context ;
		}
		
		public int getCount() {
			
			return studyGroups.size();
		}

		public Object getItem(int position) {
			
			return studyGroups.get(position);
		}

		public long getItemId(int position) {
			
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
//			List<Problem> problemList =  studyGroups.get(position).getProblemList();
			curPos = position; 
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.study_group_list, null);
			
			ListView list = (ListView) view.findViewById(R.id.list_study_group);
			list.setAdapter(new StudyGroupArrayAdapter(mContext, R.layout.problem_display,studyGroups.get(position)));
			list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					int size = studyGroups.get(curPos).getProblemList().size();
					
					if(position < size ){
						Intent intent = new Intent(mContext, StudyGroupProblemActivity.class);
						intent.putExtra("problem_server_id",studyGroups.get(curPos).getProblemList().get(position).getServerId());
						intent.putExtra("problem_user",studyGroups.get(curPos).getProblemList().get(position).getUserName());
						Log.i("problem 0Server Id", ""+String.valueOf(studyGroups.get(curPos).getProblemList().get(position).getServerId()));
						
						startActivity(intent);
						
					}else{
						String title = studyGroups.get(curPos).getNoteList().get(Math.abs(size - position)).getTitle();
						String note = studyGroups.get(curPos).getNoteList().get(Math.abs(size - position)).getContent();
						 Intent intent = new Intent(mContext, NoteDisplayActivity.class);
						 intent.putExtra("note_title", title);
						 intent.putExtra("note_content", note);
						 startActivity(intent);
					}
				}
			});
			
			
			TextView text = (TextView) view.findViewById(R.id.StudyGroupText);
			text.setText(studyGroups.get(position).getName());
			
			ImageButton leftbButton = (ImageButton) view.findViewById(R.id.left);
			int leftId = curPos!=0 ? R.drawable.left_button_background : R.drawable.left_arrow_fade;
			leftbButton.setBackgroundResource(leftId);
			leftbButton.setOnClickListener(new ImageButton.OnClickListener(){

				public void onClick(View v) {
					if(curPos != 0)
					gallery.setSelection(curPos-1);
				}
			});
			
			
			ImageButton rightbButton = (ImageButton) view.findViewById(R.id.right);
			
			int rightId = curPos != (studyGroups.size()-1) ? R.drawable.right_button_background : R.drawable.right_arrow_fade;
			
			rightbButton.setBackgroundResource(rightId);
			rightbButton.setOnClickListener(new ImageButton.OnClickListener(){

				public void onClick(View v) {
					
					if(curPos != (studyGroups.size()-1)){
					gallery.setSelection(curPos+1);
					}
				}
			});
			
			return view;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		if(id == PROGRESS_BAR){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			return progressDialog;
		}
		return null;
	}
	
	private static final int SETTINGS = Menu.FIRST;
	private static final int SEARCH = Menu.FIRST +1;
	private static final int SYNC = Menu.FIRST +2;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SETTINGS, 0, "Settings")
		  .setIcon(R.drawable.ic_menu_preferences);
		  menu.add(0, SEARCH ,0, "Search")
		  .setIcon(android.R.drawable.ic_search_category_default);
		  menu.add(0,SYNC,0,"Refresh")
		  .setIcon(R.drawable.ic_menu_refresh);
		
		return true;
	}
	  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){
		  case SETTINGS:
			  break;
			  
		  case SEARCH :
			  
			  onSearchRequested();
			  break;
		  case SYNC :
			  
			  showDialog(PROGRESS_BAR);
				 new Thread(new Runnable() {
					
					public void run() {
						
						response = UpdateStudyGroupListFromServer();
						
						handler.sendEmptyMessage(0);
					}
				}).start();
			  break;
		  }
		return true;
	}
	
	private static final int DELETE = Menu.FIRST;
	private static final int ADD_TITLE = Menu.FIRST + 1;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(0, DELETE, 0, "Delete");
		menu.add(0, ADD_TITLE, 0, "Add Title");
	}
	
	private String  UpdateStudyGroupListFromServer(){
		String ip = IpValue + "/docprocessor/sync.php";
		try {
			URL url = new URL(ip);
			Log.i("ipvalue",ip);
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			XmlHandler xmlHandler = new XmlHandler();
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.parse(new InputSource(url.openStream()));
			xmlHandler.UpdateStudyGroupList();
			
			return "Success";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "failed";
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "failed";
		} catch (SAXException e) {
			e.printStackTrace();
			return "failed";
		} catch (IOException e) {
			e.printStackTrace();
			return "failed";
		}
	}
}
