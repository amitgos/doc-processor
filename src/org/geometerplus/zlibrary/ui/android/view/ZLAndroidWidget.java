/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.optionEntries.ZLColorOptionBuilder;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidKeyUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.chaturs.models.Annotate;
public class ZLAndroidWidget extends View {
	
	private final Paint myPaint = new Paint();
	private Bitmap myMainBitmap;
	private Bitmap mySecondaryBitmap;
	private Bitmap b;
	private boolean mySecondaryBitmapIsUpToDate;
	private boolean myScrollingInProgress;
	private int myScrollingShift;
	private float myScrollingSpeed;
	private int myScrollingBound;
	
	private Paint iPaint;
	private boolean doPath = false;
	private AlertDialog.Builder builder;
	private Context mContext;
	private AlertDialog dialog;
	private EditText edit;
	//private List<RectF> rectF = new ArrayList<RectF>();
	private RectF r;
	private int FILENAME = 1; 
	private boolean New = false;
	private View view;
	private Button add,cancel;
	
	public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDrawingCacheEnabled(false);
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setDrawingCacheEnabled(false);
		mContext = context;
        iPaint = new Paint();
        iPaint.setAntiAlias(true);
        iPaint.setDither(true);
        iPaint.setColor(Color.rgb(51,102,0));
        iPaint.setStyle(Paint.Style.STROKE);
        iPaint.setStrokeJoin(Paint.Join.ROUND);
        iPaint.setStrokeCap(Paint.Cap.ROUND);
        iPaint.setStrokeWidth(3);
	}

	public ZLAndroidWidget(Context context) {
		super(context);
		setDrawingCacheEnabled(false);
	}

	public ZLAndroidPaintContext getPaintContext() {
		
		return ZLAndroidPaintContext.Instance();
	}
	
	private ZLColor getBackgroundColor(){
		
		ZLColor color;
		final ZLColorOptionBuilder builder = new ZLColorOptionBuilder();
		
		color = (ZLColor) builder.myData.myCurrentColors.get("Background");
		if(color != null){
		Log.i("Red value", " " + String.valueOf(color.Red));
		Log.i("Green value", " " + String.valueOf(color.Green));
		Log.i("Blue value", " "+String.valueOf(color.Blue));
		}
		return color;
	}
	
	// Modified functions
	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
				
		final int w = getWidth();
		final int h = getHeight();
		if ((myMainBitmap != null) && ((myMainBitmap.getWidth() != w) || (myMainBitmap.getHeight() != h))) {
			myMainBitmap = null;
			mySecondaryBitmap = null;
			System.gc();
			System.gc();
			System.gc();
		}
		
		if (myMainBitmap == null) {
			myMainBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			mySecondaryBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			mySecondaryBitmapIsUpToDate = false;
			drawOnBitmap(myMainBitmap);
		}

		if (myScrollingInProgress || (myScrollingShift != 0)) {
			onDrawInScrolling(canvas);
		} else {
			onDrawStatic(canvas);
			ZLApplication.Instance().onRepaintFinished();
		}
		
//		if(CROP != true && !rectF.isEmpty()){
//			rectF.clear();
//		}
		
		if(doPath && CROP){
			
			canvas.drawRoundRect(r, 12,12, iPaint);
		}
		
		invalidate();
	}
	
	private boolean myScreenIsTouched;
	private float mX,mY;
	public static float x1 =0,y1=0,x2=0,y2=0;
	
	private void touch_start(float x, float y){
		if(touch){
		doPath = true;
		New = true;
		x1 = 0;
		y1 = y;
		
		x2 = getWidth();
		y2 = y;
			
		r = new RectF(x1, y1, x2, y2);
		
//		rectF.add(new RectF(0,y1,getWidth(),y2));
//		AnnotateActivity.addAnnotation(new Annotate());
		
		}
	}
	
	private void touch_move(float x, float y){
		if( touch ){
		float dX = Math.abs(x - mX);
		float dY = Math.abs(y - mY);
	
		x2 = getWidth();
		
		y2 = y < 1? 1 : y;
		
		if( x2 > x1 && y2 > y1){
			r.set(x1,y1,x2, y2);
			}else if (x2 < x1 && y2 < y1){
			 r.set(x2, y2,x1, y1);
			}else if(x2 > x1 && y2 < y1){
			r.set(x1, y2,x2, y1);
			}else{
			r.set(x2, y1,x1, y2);
			}
		
//		rectF.set(rectF.size()-1,r);
//		AnnotateActivity.getRecentAnnotation().setRect(r);
		
		}
	}
	
	
	private void touch_up(){
		if( touch ){
		TOUCH = true;
		if(New){
		
		if( Math.abs((x2 -x1))>10 && Math.abs((y2 - y1))> 10 ){
		if( x2 > x1 && y2 > y1 ){
		 b= Bitmap.createBitmap(myMainBitmap,(int) x1,(int) y1, (int)(x2 - x1),(int) (y2 - y1));
		}else if(x2 < x1 && y2 < y1){
			b= Bitmap.createBitmap(myMainBitmap,(int) x2,(int) y2, (int)(x1 - x2),(int) (y1 - y2));
		}else if(x2 > x1 && y2 < y1){
			b = Bitmap.createBitmap(myMainBitmap,(int) x1,(int) y2,(int) (x2 - x1),(int)(y1 - y2));
		}else{
			b = Bitmap.createBitmap(myMainBitmap, (int)x2,(int)y1, (int)(x1 - x2),(int)(y2 - y1));
		}
		
		view = LayoutInflater.from(mContext).inflate(R.layout.save_dialog, null);
		touch = false;
  		createDialog(view);
		dialog.show();
		}else{
//			rectF.remove(rectF.size()-1);
			doPath = false;
		}
		}
		
		if(y2 < y1){
		
			float dup = y1;
			y1 = y2;
			y2=dup;
		}
		}
	}
	
	public  static boolean CROP = false , CLEAR = false,TOUCH = false ;
	private static boolean touch = true ;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int x = (int)event.getX();
		int y = (int)event.getY();

		final ZLView view = ZLApplication.Instance().getCurrentView();
		
		switch (event.getAction()) {
		
			case MotionEvent.ACTION_UP:
				if(!CROP ){
				view.onStylusRelease(x, y);
				myScreenIsTouched = false;
				}else{
				touch_up();
				invalidate();
				}
				break;
				
			case MotionEvent.ACTION_DOWN:
				if(CROP ){
				touch_start(x, y);
				invalidate();
				}else{
				view.onStylusPress(x, y);
				myScreenIsTouched = true;
				}
				break;
				
			case MotionEvent.ACTION_MOVE:
				if(CROP ){
				touch_move(x, y);
				invalidate();
				}else{
				view.onStylusMovePressed(x, y);
				}
				break;
		}
		return true;
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
      
     private boolean checkIfFileExists(String fileName){
    	 
    	 String path  = Environment.getExternalStorageDirectory() + "/Images/";
    	 File file = new File(path + fileName);
    	 if(file.exists()){
    		 return true;
    	 }
    	 return false;
     }
      
  	private void createDialog(View view){
  		Button addButton , cancelButton;
  		edit = (EditText) view.findViewById(R.id.Savecontext);
  		
  		addButton = (Button) view.findViewById(R.id.addButton);
  		cancelButton = (Button) view.findViewById(R.id.cancelButton);
  		
  		builder = new AlertDialog.Builder(mContext);
  		builder.setTitle("Annotate");
  		builder.setView(view);
  		builder.setCancelable(false);
  		
  		addButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				
  				String  text = edit.getText().toString().trim();
  				Annotate annotate = new Annotate();
  				if(text.trim().length() == 0){
  					edit.setError("Please Enter Some Text !");
  					 return;
  				}
  				String fileName ;
  				String path;
  				Log.i("Text", text);
  				if(New){
  					fileName = "Image" + Integer.toString(FILENAME++)+".jpg";
  				
  				while(checkIfFileExists(fileName)){
  					fileName = "Image" + Integer.toString(FILENAME++)+".jpg";
  				}
  				path = saveBitmap(b,fileName);
  				annotate.setLocalImagePath(path);
//  				AnnotateActivity.getRecentAnnotation().setPath(path);
  				}
  					annotate.setText(text);
  				AnnotateActivity.addAnnotation(annotate);
//  				AnnotateActivity.getRecentAnnotation().setText(text);
  				touch = true ;
  				CROP = false ;
  				doPath = false;
  				InputMethodManager inputManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
  				inputManager.hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
  				invalidate();
   				dialog.dismiss();
			}
  		});
  		
  		cancelButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				
//				if(!rectF.isEmpty() && New){
//  					rectF.remove(rectF.size()-1);
//  					AnnotateActivity.removeAnnotation();
//  				}
  				
  				InputMethodManager inputManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
  				inputManager.hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
  				touch = true;
  				doPath = false;
  				invalidate();
   				dialog.dismiss();
  			}
  		});
  		
//  	
  		dialog =  builder.create();
  	}

  	// end of modified functions
  	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		if (myScreenIsTouched) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			myScrollingInProgress = false;
			myScrollingShift = 0;
			myScreenIsTouched = false;
			view.onScrollingFinished(ZLView.PAGE_CENTRAL);
			setPageToScroll(ZLView.PAGE_CENTRAL);
		}
	}

	private void onDrawInScrolling(Canvas canvas) {
		
		final int w = getWidth();
		final int h = getHeight();
		final ZLAndroidPaintContext context = ZLAndroidPaintContext.Instance();

		boolean stopScrolling = false;
		if (myScrollingInProgress) {
			myScrollingShift += (int)myScrollingSpeed;
			if (myScrollingSpeed > 0) {
				if (myScrollingShift >= myScrollingBound) {
					myScrollingShift = myScrollingBound;
					stopScrolling = true;
				}
			} else {
				if (myScrollingShift <= myScrollingBound) {
					myScrollingShift = myScrollingBound;
					stopScrolling = true;
				}
			}
			myScrollingSpeed *= 1.5;
		}
		
		final boolean horizontal =
			(myViewPageToScroll == ZLView.PAGE_RIGHT) || 
			(myViewPageToScroll == ZLView.PAGE_LEFT);
		canvas.drawBitmap(
			myMainBitmap,
			horizontal ? myScrollingShift : 0,
			horizontal ? 0 : myScrollingShift,
			myPaint
		);
		
		final int size = horizontal ? w : h;
		int shift = (myScrollingShift < 0) ? (myScrollingShift + size) : (myScrollingShift - size);
		canvas.drawBitmap(
			mySecondaryBitmap,
			horizontal ? shift : 0,
			horizontal ? 0 : shift,
			myPaint
		);
		
		if (stopScrolling) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			if (myScrollingBound != 0) {
				Bitmap swap = myMainBitmap;
				myMainBitmap = mySecondaryBitmap;
				mySecondaryBitmap = swap;
				mySecondaryBitmapIsUpToDate = false;
				view.onScrollingFinished(myViewPageToScroll);
				ZLApplication.Instance().onRepaintFinished();
			} else {
				view.onScrollingFinished(ZLView.PAGE_CENTRAL);
			}
			setPageToScroll(ZLView.PAGE_CENTRAL);
			myScrollingInProgress = false;
			myScrollingShift = 0;
		} else {
			if (shift < 0) {
				shift += size;
			}
			// TODO: set color
			myPaint.setColor(Color.rgb(127, 127, 127));
			if (horizontal) {
				canvas.drawLine(shift, 0, shift, h + 1, myPaint);
			} else {
				canvas.drawLine(0, shift, w + 1, shift, myPaint);
			}
			if (myScrollingInProgress) {
				postInvalidate();
			}
		}
	}

	private int myViewPageToScroll = ZLView.PAGE_CENTRAL;
	private void setPageToScroll(int viewPage) {
		if (myViewPageToScroll != viewPage) {
			myViewPageToScroll = viewPage;
			mySecondaryBitmapIsUpToDate = false;
		}
	}

	void scrollToPage(int viewPage, int shift) {
		switch (viewPage) {
			case ZLView.PAGE_BOTTOM:
			case ZLView.PAGE_RIGHT:
				shift = -shift;
				break;
		}

		if (myMainBitmap == null) {
			return;
		}
		if (((shift > 0) && (myScrollingShift <= 0)) ||
			((shift < 0) && (myScrollingShift >= 0))) {
			mySecondaryBitmapIsUpToDate = false;
		}
		myScrollingShift = shift;
		setPageToScroll(viewPage);
		drawOnBitmap(mySecondaryBitmap);
		postInvalidate();
	}

	void startAutoScrolling(int viewPage) {
		if (myMainBitmap == null) {
			return;
		}
		myScrollingInProgress = true;
		switch (viewPage) {
			case ZLView.PAGE_CENTRAL:
				switch (myViewPageToScroll) {
					case ZLView.PAGE_CENTRAL:
						myScrollingSpeed = 0;
						break;
					case ZLView.PAGE_LEFT:
					case ZLView.PAGE_TOP:
						myScrollingSpeed = -3;
						break;
					case ZLView.PAGE_RIGHT:
					case ZLView.PAGE_BOTTOM:
						myScrollingSpeed = 3;
						break;
				}
				myScrollingBound = 0;
				break;
			case ZLView.PAGE_LEFT:
				myScrollingSpeed = 3;
				myScrollingBound = getWidth();
				break;
			case ZLView.PAGE_RIGHT:
				myScrollingSpeed = -3;
				myScrollingBound = -getWidth();
				break;
			case ZLView.PAGE_TOP:
				myScrollingSpeed = 3;
				myScrollingBound = getHeight();
				break;
			case ZLView.PAGE_BOTTOM:
				myScrollingSpeed = -3;
				myScrollingBound = -getHeight();
				break;
		}
		if (viewPage != ZLView.PAGE_CENTRAL) {
			setPageToScroll(viewPage);
		}
		drawOnBitmap(mySecondaryBitmap);
		postInvalidate();
	}

	private void drawOnBitmap(Bitmap bitmap) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (view == null) {
			return;
		}

		if (bitmap == myMainBitmap) {
			mySecondaryBitmapIsUpToDate = false;
		} else if (mySecondaryBitmapIsUpToDate) {
			return;
		} else {
			mySecondaryBitmapIsUpToDate = true;
		}

		final int w = getWidth();
		final int h = getHeight();
		final ZLAndroidPaintContext context = ZLAndroidPaintContext.Instance();

		Canvas canvas = new Canvas(bitmap);
		context.beginPaint(canvas);
		final int scrollbarWidth = view.showScrollbar() ? getVerticalScrollbarWidth() : 0;
		context.setSize(w, h, scrollbarWidth);
		view.paint((bitmap == myMainBitmap) ? ZLView.PAGE_CENTRAL : myViewPageToScroll);
		context.endPaint();
	}

	private void onDrawStatic(Canvas canvas) {
		drawOnBitmap(myMainBitmap);
		canvas.drawBitmap(myMainBitmap, 0, 0, myPaint);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if(!CROP){
		if (event.getAction() == MotionEvent.ACTION_DOWN ) {
			onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
		} else {
			ZLApplication.Instance().getCurrentView().onTrackballRotated((int)(10 * event.getX()), (int)(10 * event.getY()));
		}
		}
		return true;
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(!CROP){
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return ZLApplication.Instance().doActionByKey(ZLAndroidKeyUtil.getKeyNameByCode(keyCode));
			case KeyEvent.KEYCODE_DPAD_DOWN:
				ZLApplication.Instance().getCurrentView().onTrackballRotated(0, 1);
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				ZLApplication.Instance().getCurrentView().onTrackballRotated(0, -1);
				return true;
			default:
				return false;
		}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(!CROP){
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return true;
			default:
				return false;
		}
		}
		return false;
	}

	protected int computeVerticalScrollExtent() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.showScrollbar()) {
			return 0;
		}
		if (myScrollingInProgress || (myScrollingShift != 0)) {
			final int from = view.getScrollbarThumbLength(ZLView.PAGE_CENTRAL);
			final int to = view.getScrollbarThumbLength(myViewPageToScroll);
			final boolean horizontal =
				(myViewPageToScroll == ZLView.PAGE_RIGHT) || 
				(myViewPageToScroll == ZLView.PAGE_LEFT);
			final int size = horizontal ? getWidth() : getHeight();
			final int shift = Math.abs(myScrollingShift);
			return (from * (size - shift) + to * shift) / size;
		} else {
			return view.getScrollbarThumbLength(ZLView.PAGE_CENTRAL);
		}
	}

	protected int computeVerticalScrollOffset() {
		
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.showScrollbar()) {
			return 0;
		}
		if (myScrollingInProgress || (myScrollingShift != 0)) {
			final int from = view.getScrollbarThumbPosition(ZLView.PAGE_CENTRAL);
			final int to = view.getScrollbarThumbPosition(myViewPageToScroll);
			final boolean horizontal =
				(myViewPageToScroll == ZLView.PAGE_RIGHT) || 
				(myViewPageToScroll == ZLView.PAGE_LEFT);
			final int size = horizontal ? getWidth() : getHeight();
			final int shift = Math.abs(myScrollingShift);
			return (from * (size - shift) + to * shift) / size;
		} else {
			return view.getScrollbarThumbPosition(ZLView.PAGE_CENTRAL);
		}
	}
	
	protected int computeVerticalScrollRange() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.showScrollbar()) {
			return 0;
		}
		return view.getScrollbarFullSize();
	}
	
}
