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

package com.chaturs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaturs.models.DatabaseHandler;

public class CreateNotePaint extends Activity
        implements ColorPickerDialog.OnColorChangedListener {    
	
		private Bitmap  mBitmap;
		private AlertDialog dialog;
		private MyView myView;
		private FileInputStream in;
		private BufferedInputStream buf;
		private static Boolean EDIT = false;
		private static final String TAG = "CreateNotePaint";
		private static  int pos;
		private int mState;
		private static final int STATE_EDIT = 0;
		private static final int STATE_INSERT = 1;
		private int orientation ;
		private int width , height ;
		 private ColorDrawable cd = new ColorDrawable(Color.WHITE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    
       
        orientation = getWindowManager().getDefaultDisplay().getOrientation();
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        
        final Intent intent = getIntent();
        final String Action = intent.getAction();
        
        if(Intent.ACTION_EDIT.equals(Action)){
        	mState = STATE_EDIT;
        	 pos = intent.getExtras().getInt("Position");
        	 setTitle(NotePaintList.getPaint(pos).getTitle());
        	
        	Log.i("Testing",String.valueOf(pos));
      	BitmapFactory.Options opts =new BitmapFactory.Options();
  		opts.inJustDecodeBounds=true;
  		
      	NotePaint notePaint = NotePaintList.getPaint(pos);
      	
      try{
    	 File file  = new File(notePaint.getPath());
    	 if(file.exists()){
	      	 in = new FileInputStream(file);
	      	 mBitmap = BitmapFactory.decodeStream(in);
	      	 mBitmap = mBitmap.copy(Bitmap.Config.ARGB_4444, true);
	      	 
	      	if(in !=  null){
	      		in.close();
      	 }
    	 }else{
//      		mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
//        	mBitmap.eraseColor(Color.WHITE);
    		 Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
    		 finish();
    		 return;
      	 }
      	}catch(Exception e){
      		Log.e("Error Reading File",e.toString());
      		finish();
      	}
      	
        }else if(Intent.ACTION_INSERT.equals(Action)){
        	mState = STATE_INSERT;
        	mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
        	mBitmap.eraseColor(Color.WHITE);
        	
        }else{
        	 Log.e(TAG, "Unknown action, exiting");
             finish();
             return;
        }
        
        myView = new MyView(this);
        setContentView(myView);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(4);
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        
    }
    
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    public class MyView extends View {
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        
        public MyView(Context c) {
            super(c);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(mBitmap,0 ,0, mBitmapPaint);
           	canvas.drawPath(mPath, mPaint);
           	
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 2;
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        
        private void touch_move(float x, float y) {
         
        	float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        
        private void touch_up() {
          
        	mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) {
         
        	float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    
    private void New(){
    	
    	mBitmap.eraseColor(Color.WHITE);
    	myView.invalidate();
    }
    
    private String save(String fileName){
    	
		String path  = Environment.getExternalStorageDirectory() + "/Images/";
		File images = new File(path.toString() + "/PaintImages");
		
		images.mkdirs();
		File outputfile = new File(images, fileName);
		try {
			outputfile.createNewFile();
			FileOutputStream out = new FileOutputStream(outputfile);
			BufferedOutputStream  bos = new BufferedOutputStream(out);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
		} catch (IOException e1) {
			e1.printStackTrace();
//			Toast.makeText(this, "File "+ fileName + "not saved" , Toast.LENGTH_SHORT).show();
			return null;
		}
		return path + "PaintImages/" + fileName;
    }
    
    private void saveToPath(String path){
    	
		File outputfile = new File(path);
		try {
			
			FileOutputStream out = new FileOutputStream(outputfile);
			
			BufferedOutputStream  bos = new BufferedOutputStream(out);
		
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
			mBitmap.recycle();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
                                
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
    private static final int SAVE_MENU_ID = Menu.FIRST + 5;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 6;
    private static final int SAVE_ID = 100;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, COLOR_MENU_ID, 0, "Color")
        .setIcon(R.drawable.color);
        menu.add(0, CLEAR_MENU_ID, 0, "Clear")
        .setIcon(R.drawable.ic_menu_clear);
        menu.add(0, SAVE_MENU_ID, 0, "Save")
        .setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss")
         .setIcon(R.drawable.emboss);
        menu.add(0, BLUR_MENU_ID, 0, "Blur")
         .setIcon(R.drawable.blur);
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
        
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
                
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
                
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
                
                
            case CLEAR_MENU_ID:
            		
            		New();
            		
            	return true;
            
            case SAVE_MENU_ID:
            	
            		showDialog(SAVE_ID);
            		
            	
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
@Override
protected Dialog onCreateDialog(int id) {
	
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	switch(id){
	
	case SAVE_ID:
		
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_title	,null);
		final EditText editTitle = (EditText) view.findViewById(R.id.edit);
		final Button posButton = (Button) view.findViewById(R.id.ok);
		final Button negButton = (Button) view.findViewById(R.id.cancel);
		if(mState == STATE_EDIT){
			editTitle.setText(NotePaintList.getPaint(pos).getTitle());
		}
		builder.setTitle("Save the Paint !");
		builder.setView(view);
		posButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				
				String title = editTitle.getText().toString();
			
				if(title.trim().length() == 0 ){
					editTitle.setError("Please enter some text");
					return;
				}
				
				if(mState == STATE_INSERT && !checkIfTitleAvailable(title)){
					editTitle.setError("Entered Title already exists !");
					return;
				}
				
				DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
				if(mState == STATE_INSERT){
					String path;
					if((path = save(title.concat(".jpg"))) != null){
						databaseHandler.insertPaint(new NotePaint(title, path));
					}else{
						Toast.makeText(getApplicationContext(),  "Failed to save " + "' "+ title.concat("jpg")+ " '", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						return;
					}
				}else if(mState == STATE_EDIT){
					NotePaintList.getPaint(pos).setTitle(editTitle.getText().toString());
					saveToPath(NotePaintList.getPaint(pos).getPath());
					databaseHandler.updatePaint(NotePaintList.getPaint(pos));
				}
				finish();
				
				dialog.dismiss();
			}
			
		});
		
		negButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				dialog.dismiss();
			}
			
		});
	
		dialog = builder.create();
		return dialog;
	}
	
	return super.onCreateDialog(id);
}

	private boolean checkIfTitleAvailable(String text){
		
	List<NotePaint> list = new ArrayList<NotePaint>();
	
		DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
		list = databaseHandler.listNotePaints();
		
		Iterator< NotePaint> iterator = list.iterator();
		while(iterator.hasNext()){
			
			NotePaint notepaint = iterator.next();
			
			if(text.equals(notepaint.getTitle())){
				return false;
			}
		}
		return true;
	}
}
