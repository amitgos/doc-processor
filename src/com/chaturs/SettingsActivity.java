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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private static final String KEY_USERNAME = "key_user";
	private static final String  DOC_PREFERENCE ="preferences";
	private static final String KEY_IPVALUE = "key_ipvalue";
	private SharedPreferences preferences ;
	private EditText userEditText,ipEditText;
	private String ipvalue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		preferences = getSharedPreferences(DOC_PREFERENCE, Context.MODE_PRIVATE);
		 ipvalue = preferences.getString(KEY_IPVALUE, "");
		 userEditText  = (EditText)findViewById(R.id.username);
  		 ipEditText = (EditText) findViewById(R.id.ipvalue); 
  		 ipEditText.setText(ipvalue);
  		Button setButton  = (Button) findViewById(R.id.Set);
  		setButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				String response;
				String userName = userEditText.getText().toString().trim();
				Log.i("test","user: "+userName);
				if(userName.length() <= 0){
					userEditText.setError("Enter some text");
					return;
				}else{
					response = checkNewUser(userName);
					if(response.equals("Success")){
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString( KEY_USERNAME,userName);
						editor.commit();
						Toast.makeText(getApplicationContext(), " Registered  Successfully !", Toast.LENGTH_SHORT).show();
						
					}else if(response.equals("failed")){
						
						Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
						
					}else{
						userEditText.setError("User name entered is already exists !!");
						return;
					}
				}
			}
  			
  		});
  		
  		Button saveButton  = (Button) findViewById(R.id.Save);
  		saveButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				
				String text = ipEditText.getText().toString().trim();
				SharedPreferences.Editor editor =  preferences.edit();
				editor.putString(KEY_IPVALUE, text);
				editor.commit();
				
				Toast.makeText(getApplicationContext(), "IP adress changed", Toast.LENGTH_SHORT).show();
			}
  		});
	}
	
	public String checkNewUser(String userName) {
		
		 ipvalue = preferences.getString(KEY_IPVALUE, "");
			HttpClient httpclient = new DefaultHttpClient();
			String ip = ipvalue+"/docprocessor/adduser.php?";
			HttpPost httppost = new HttpPost(ip);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", userName));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httppost, responseHandler);
				
				Log.i("test", "" + response);
				return response;
			} catch (ClientProtocolException e) {
				Log.e("test", "" + e.getMessage());
				return "failed";
			} catch (IOException e) {
				Log.e("test", "" + e.getMessage());
				return "failed";
			}
		}
}
