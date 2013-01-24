package org.geometerplus.android.fbreader;


import java.io.File;

import org.geometerplus.fbreader.fbreader.FBReader;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.filesystem.ZLPhysicalFile;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.android.fbreader.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MyBookreaderActivity extends ZLAndroidActivity{
	
	private ZLPhysicalFile physicalFile;
    /** Called when the activity is first created. */
	private Button button;
	private String path;
	private String fileName;
	private Book book;
	private FBReader freader;
	public static AlertDialog SaveDialog;
	public static boolean PopDialog = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      
        Bundle bundle = getIntent().getExtras();
    	String path =  bundle.getString("book_path");
    	
        
//        String p = Environment.getExternalStorageDirectory() + "/Books/Demos/Smashwords.com/books/download/13907/8/latest/"
//        +"0/1/covenant-sojourner-book-2.epub";
//        String p1 =  Environment.getExternalStorageDirectory() + "/Books/Lorentz - The Einstein Theory of Relativity.epub";
       
//        Log.i("Book Path", p1);
        
		ZLApplication.Instance().openFile(new ZLPhysicalFile(new File(path)));
//		startActivity(new Intent(this, ZLAndroidApplication.class));
		
    }
    
    @Override
	protected ZLApplication createApplication(String fileName) {
		new SQLiteBooksDatabase();
		String[] args = (fileName != null) ? new String[] { fileName } : new String[0];
		return new org.geometerplus.fbreader.fbreader.FBReader(args);
	}
}