package org.geometerplus.android.fbreader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Problem;
import com.chaturs.models.StudyGroup;

public class AnnotateActivity extends Activity{

		private ImageView image;
		private ScrollView scrollView;
		private LinearLayout layout,layout2;
		private TextView tv , title;
		private ImageView iv;
		private View view;
		public static List<Annotate> annotateList = new ArrayList<Annotate>();
		private static final int  SEARCH= Menu.FIRST+1, STUDY_GROUP = Menu.FIRST+2;
		private AlertDialog dialog ,problemNameDialog;
		private static final int SELECT_STUDY_GROUP = 100;
		private static final int PROGRESS_DIALOG_KEY = 101;
		private List<String> studyGroupNames = new ArrayList<String>();
		private List<Long> studyGroupServerIds = new ArrayList<Long>();
		private CharSequence[] GROUP = new CharSequence[10];
		private Problem problem;
		private List<StudyGroup> selectedStudyGroupList = new ArrayList<StudyGroup>();
		private List<StudyGroup> studyGroupList = new ArrayList<StudyGroup>();
		private static final String KEY_USERNAME = "key_user";
		private static final String  DOC_PREFERENCE ="preferences";
		private static final String KEY_IPVALUE = "key_ipvalue";		
		private SharedPreferences preferences ;
		private SharedPreferences.Editor edit;
		private String problemName;
		private String IpValue;
		private String response;
		
		public static void addAnnotation(Annotate annotate) {
			
			if(null != annotate) {
				annotateList.add(annotate);
			}
		}
		
		public static List<Annotate>getListAnnotation(){
			
			return annotateList;
		}
		
		public static void removeAnnotation(){
			
			if(!annotateList.isEmpty()){
				annotateList.remove(annotateList.size()-1);
			}
		}
		
		public static void removeAnnotation(int index) {
			if(annotateList.size() > index) {
				annotateList.remove(index);
			}
		}
		
		public static Annotate getAnnotatate(int position){
			
			 return annotateList.isEmpty() ? null : annotateList.get(position); 
		}
		
		public static Annotate getRecentAnnotation() {
			
			return annotateList.get(annotateList.size()-1);
		}
		
		public static int getIndex(){
			
			return  annotateList.isEmpty()? 0 : annotateList.size()-1;
		}
		public static boolean isEmpty(){
			
		return	annotateList.isEmpty() ? true : false;
		}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("Annotation List");
		
		setContentView(R.layout.display_annotation);
		
		 preferences = getSharedPreferences(DOC_PREFERENCE,Context.MODE_PRIVATE);
		 IpValue = preferences.getString(KEY_IPVALUE, "");
		
		ZLAndroidWidget a = new ZLAndroidWidget(this);
		
		layout = (LinearLayout) findViewById(R.id.scrollLayout);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		
		for(int i=0;i<annotateList.size();i++){
			
		 view =inflater.inflate(R.layout.annotate, null);
		title = (TextView) view.findViewById(R.id.annotation_title);
		 tv = (TextView) view.findViewById(R.id.annotatedText);
		 iv = (ImageView) view.findViewById(R.id.annotatedImage);
		  String text = annotateList.get(i).getText();
		  title.setText("Annotation" + " " + Integer.toString(i+1) );
		 tv.setText(text);
		 iv.setImageDrawable(Drawable.createFromPath(annotateList.get(i).getLocalImagePath()));
		 
		 layout.addView(view);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE,SEARCH,Menu.NONE,"Search")
		.setIcon(android.R.drawable.ic_search_category_default);
		menu.add(Menu.NONE,STUDY_GROUP,Menu.NONE,"Send to Study Group(s)")
		.setIcon(R.drawable.ic_menu_studygroup);
		
	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case STUDY_GROUP:
			showDialogForUserName();
			break;
		case SEARCH :
			onSearchRequested();
			break;
		}

		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		
//		if(!annotateList.isEmpty())
//			annotateList.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(!annotateList.isEmpty())
//		annotateList.clear();
		
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissDialog(PROGRESS_DIALOG_KEY);
			if(response != null){
				Toast.makeText(getApplicationContext(), "Success ", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "Sent failed" , Toast.LENGTH_SHORT).show();
			}
		
			selectedStudyGroupList.clear();
			if(!annotateList.isEmpty())
				annotateList.clear();
				ZLAndroidWidget.CROP = false;
				finish();
		}
		
	  };
	
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
					String unknown = "UnKnown";
					if(selectedStudyGroupList.isEmpty()){
						Toast.makeText(getApplicationContext(), 
								"No option selected to send", Toast.LENGTH_SHORT).show();
						return;
					}
					showDialog(PROGRESS_DIALOG_KEY);
					new Thread(new Runnable() {
						
						public void run() {
							response = send();					
							
							handler.sendEmptyMessage(0);
						}
					}).start();
						
				}
			});	
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
		
			dialog = builder.create();
			return dialog
			;
		case PROGRESS_DIALOG_KEY:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("sending....");
			progressDialog.setIndeterminate(true);
			return progressDialog;
	}
	return null;
	}
	
	
	private String  send(){
		
		DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
		
		StringBuffer Ids = new StringBuffer();
		for(int i=0;i<selectedStudyGroupList.size();i++){
			if(Ids.length() > 0){
				Ids.append("|");
			}
			long id = selectedStudyGroupList.get(i).getServerId();
			Ids.append(id);
		}
		String pid=null;
		String user = preferences.getString(KEY_USERNAME, "android");
		
		for(int i=0;i<annotateList.size();i++){

			Annotate annotate = annotateList.get(i);
			if(i==0){
				pid = post(0,annotate,Ids.toString(),problemName,user);
				Log.i("Test post","pid value returned  "+pid);
			}else if(pid != null){
				post(Long.valueOf(pid),annotate,Ids.toString(),problemName,user);
				Log.i("Test post","pid value returned  "+pid);
			}
		}
		return pid;
	}
		
	  private void showDialogForUserName(){
	    	
	  		View view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null);
	  		final EditText edit = (EditText) view.findViewById(R.id.edit);
	  		Button okButton = (Button) view.findViewById(R.id.ok);
	  		Button cancelButton  = (Button) view.findViewById(R.id.cancel);
	  		
	  		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  		builder.setTitle("Enter Problem Name");
	  		builder.setView(view);
	  		builder.setCancelable(false);
	  	
	  		okButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					problemName = edit.getText().toString();
					if(problemName.length() < 1){
						edit.setError("Enter Some Text !!");
						return;
					}
					
					problemNameDialog.dismiss();
					showDialog(SELECT_STUDY_GROUP);
				}
			});
	  		
	  		cancelButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					problemNameDialog.dismiss();
				}
			});
	  		
	  		problemNameDialog  = builder.create();
	  		 problemNameDialog.setCancelable(false);
	  		problemNameDialog.show();
	    }

    
    public String post(long _pid,Annotate annotate,String Ids,String problemName,String user) {
	
    	dismissDialog(SELECT_STUDY_GROUP);
    	HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		
		
		String title = URLEncoder.encode(problemName);
		String comment=URLEncoder.encode(annotate.getText());
		String pathToOurFile = URLEncoder.encode(annotate.getLocalImagePath());
		String fileName = "add.jpg";
		String pid = URLEncoder.encode(String.valueOf(_pid));
		
//		String urlServer = "http://www.compliantbox.com/docprocessor/addproblem.php?pid=0&title=rqwer&comment=comment&groups=2|4|5user=" + URLEncoder.encode("New User");
		String urlServer = IpValue + "/docprocessor/addproblem.php?pid=" + pid +"&title="+title+"&comment="+comment+"&groups="+Ids+"&user=" + URLEncoder.encode(user);
		Log.i("URLSERVER",""+urlServer);
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "***32423412**";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					annotate.getLocalImagePath()));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\""
							+ fileName + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			String serverResponseMessage = connection.getResponseMessage();
			String returnRespose = "";
			try {
			    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    
			    String str;
			    while ((str = in.readLine()) != null) {
			    	returnRespose = returnRespose + str;
			    }
			    in.close();
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			
			Log.i("test", "" + serverResponseMessage +"Return Response: " + returnRespose);
			return returnRespose;
		} catch (Exception ex) {
			Log.e("test", ex.getMessage());
		}
		return null;
	}
	
}
