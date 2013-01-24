package com.chaturs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Solution;

public class StudyGroupProblemActivity extends Activity{
	
	private static final int SEND = Menu.FIRST;
	private static final int REPLY = Menu.FIRST +1 ;
	
	private List<Annotate> annotateList;
	private List<Solution> solutionList ;
	private LinearLayout layout;
	private TextView tv , title , user , content;
	private ImageView iv ;
	private View view;
	private Dialog dialog;
	
	private Thread thread;
	private String serverPath ;
	private DatabaseHandler databaseHandler ;
	private static long problemId;
	private static  String userName;
	private static final int PROGRESS_BAR_LOAD = 101;
	private static final int PROGRESS_BAR_SEND = 102;
	private static final int NOTE_EDITOR = 103;
	private static final int LOAD_ANNOTATION = 1001;
	private static final int SEND_SOLUTION  = 1002;
	private static final String KEY_USERNAME = "key_user";
	private static final String  DOC_PREFERENCE ="preferences";
	private static final String KEY_IPVALUE = "key_ipvalue";
	
	private SharedPreferences preferences;
	private ProgressDialog progressDialog ;
	private Solution solution;
	private String response;
	private String editorNote;
	private View header_view;
	private LayoutInflater inflater ;
	private  EditText editor;
	private  View solutionview ;
	private String IpValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.problem_view);
		databaseHandler = DatabaseHandler.getInstance();
			preferences =getSharedPreferences(DOC_PREFERENCE, Context.MODE_PRIVATE);
			userName = preferences.getString(KEY_USERNAME, "android");
			IpValue = preferences.getString(KEY_IPVALUE, "");
			serverPath = IpValue + "/docprocessor/imgs/";
			Intent intent = getIntent();
			problemId = intent.getExtras().getLong("problem_server_id");
			Log.i("problem 0Server Id", ""+String.valueOf(problemId));
			
			inflater = LayoutInflater.from(this);
			view =inflater.inflate(R.layout.solution, null);
			header_view = inflater.inflate(R.layout.solutions_header, null);
			solutionview =inflater.inflate(R.layout.solution, null);
			layout = (LinearLayout) findViewById(R.id.StudyGroupProblemLayout);
		

		showDialog(PROGRESS_BAR_LOAD);
		 new Thread(new Runnable() {
				public void run() {
					databaseHandler = DatabaseHandler.getInstance();
					fetchImages();
					handler.sendEmptyMessage(LOAD_ANNOTATION);
				}
			}).start();
	}
	
	final Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			
			case LOAD_ANNOTATION:
				displayAnnotations();
				displaySolutions();
				dismissDialog(PROGRESS_BAR_LOAD);
				break;
		
			case SEND_SOLUTION:
				dismissDialog(PROGRESS_BAR_SEND);
			if(response.equals("Success")){
				Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
				displayAnnotations();
				displaySolutions();
					}else{
					Toast.makeText(getApplicationContext(), "sent failed", Toast.LENGTH_SHORT).show();
				}
				
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch(id){
		case PROGRESS_BAR_LOAD:
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading..");
			progressDialog.setIndeterminate(true);
			return progressDialog;
			
		case PROGRESS_BAR_SEND:
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Sending..");
			progressDialog.setIndeterminate(true);
			return progressDialog;
			
		case NOTE_EDITOR:
			return createDialogForReply();
			
		}
		return null;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if(id == NOTE_EDITOR){
			editor.setText("");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,REPLY,Menu.NONE,"Reply");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case REPLY:
			
			showDialog(NOTE_EDITOR);
			break;
		default:
			break;
		}
		return true;
	}
	
	 private Bitmap downloadImageFromserver(String imageUrl){
	    	Bitmap bitmap = null;
	    	URL url = null;
	    	
	    	Log.i("Image Url",""+imageUrl);
			try {
				url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
	    }
	 
	 private void fetchImages(){
		
		 annotateList = databaseHandler.listAnnotations(problemId);
		 for(int i=0;i<annotateList.size();i++){

			Annotate annotate = annotateList.get(i);
			
			 if(annotate.getLocalImagePath() != null && annotate.getLocalImagePath().length()>0){
			 }else{
				 Bitmap bitmap = downloadImageFromserver(annotate.getServerImagePath());
					 if(bitmap != null){
						String fileName = annotate.getServerImagePath();
						fileName = fileName.replace(serverPath, "");
						 String path = saveBitmap(bitmap, fileName);
						 
						 if(path != null && path.length() > 0){
							 annotate.setLocalImagePath(path);
							 databaseHandler.updateLocalImagePath(annotate);
						 }
					 }
			 }
			}
	 }
	 
	 private void displayAnnotations(){
		 LayoutInflater inflater = LayoutInflater.from(this);
		 annotateList = databaseHandler.listAnnotations(problemId);
		 for(int i=0;i<annotateList.size();i++){
			
				view =inflater.inflate(R.layout.annotate1, null);
				 title = (TextView) view.findViewById(R.id.annotation_title);
				 tv = (TextView) view.findViewById(R.id.annotatedText);
				 iv = (ImageView) view.findViewById(R.id.annotatedImage);
				Annotate annotate = annotateList.get(i);
			 tv.setText(annotate.getText());
			 title.setText("Annotation" + " " + Integer.toString(i+1) );
			
			 if(annotate.getLocalImagePath() != null && annotate.getLocalImagePath().length()>0){
				 iv.setImageDrawable(Drawable.createFromPath(annotate.getLocalImagePath()));
			 }
//			 else{
//				 Bitmap bitmap = downloadImageFromserver(annotate.getServerImagePath());
//					 if(bitmap != null){
//						String fileName = annotate.getServerImagePath();
//						fileName = fileName.replace(serverPath, "");
//						 String path = saveBitmap(bitmap, fileName);
//						 
//						 if(path != null && path.length() > 0){
//							 annotate.setLocalImagePath(path);
//							 databaseHandler.updateLocalImagePath(annotate);
//						 }
//					iv.setImageDrawable(Drawable.createFromPath(annotate.getLocalImagePath()));
//					 }
//			 }
			 layout.addView(view);
			}
	 }
	 
	 private void displaySolutions(){
		
		LayoutInflater inflater = LayoutInflater.from(this);
		solutionList = databaseHandler.listSolutions(problemId);
		if(!solutionList.isEmpty()){
		layout.addView(header_view);
		}
		 for(int i=0;i<solutionList.size();i++){
			 
				view =inflater.inflate(R.layout.solution, null);
				 user = (TextView) view.findViewById(R.id.user);
				 content = (TextView) view.findViewById(R.id.text);
				Solution solution = solutionList.get(i);
				user.setText("-  " +solution.getUser());
				content.setText(solution.getSolution());
			 layout.addView(view);
			}
	 }
	 
	 private String  saveBitmap(Bitmap bitmap,String fileName){
			
			String path  = Environment.getExternalStorageDirectory() + "/";
			
			File images = new File(path.toString() + "/Images");
			images.mkdirs();
			File outputfile = new File(images, fileName);
			try {
				
				FileOutputStream out = new FileOutputStream(outputfile);
				
				BufferedOutputStream  bos = new BufferedOutputStream(out);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return path + "Images/" + fileName;
		}
	 
	  public String postData(Solution solution) {  
	    	
//		  dismissDialog(NOTE_EDITOR);
	    	
	        HttpClient httpclient = new DefaultHttpClient();  
	        HttpPost httppost = new HttpPost(IpValue + "/docprocessor/postreply.php");  
	      
	        String user = solution.getUser();
	        String content = solution.getSolution(); 
	        long problemId = solution.getProblemId();
	        Log.i("From User", ""+user);
	        
	        try {    
	           
	        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	            nameValuePairs.add(new BasicNameValuePair("problem_id", Long.toString(problemId)));  
	            nameValuePairs.add(new BasicNameValuePair("reply", content));
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
	  
	  private Dialog createDialogForReply(){
		  
		  final View editorView = inflater.inflate(R.layout.editor, null);
			
			Button send,clear,cancel;
			
			
			send = (Button) editorView.findViewById(R.id.send);
			clear = (Button)editorView.findViewById(R.id.clear);
			cancel = (Button) editorView.findViewById(R.id.cancel);
			editor = (EditText)editorView.findViewById(R.id.editor);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Editor");
			builder.setView(editorView);
			
			send.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					editorNote = editor.getText().toString();
					
					solution = new Solution(0, 0, problemId,editorNote, userName);
					
					dialog.dismiss();
				
					showDialog(PROGRESS_BAR_SEND);
					new Thread(new Runnable() {
						
						public void run() {
							response = postData(solution);
							UpdateStudyGroupListFromServer();
							solutionList = databaseHandler.listSolutions(problemId);
							layout.removeAllViewsInLayout();
							handler.sendEmptyMessage(SEND_SOLUTION);
						}
					}).start();
					
				}
			});
			
			clear.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					
					editor.setText("");
					
				}
			});
			
			cancel.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					
					dialog.dismiss();
				}
			});
			
			dialog = builder.create();
			return dialog;
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
