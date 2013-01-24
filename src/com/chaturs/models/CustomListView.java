package com.chaturs.models;

import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.BookShelfActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ListView;

public class CustomListView extends ListView {

	private static final int MIN_LENGTH = 80;
	private static final int MAX_Y_OFF = 80;
	private static final int VELOCITY_LIMIT = 50;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	private void initializeGestureRecognizer() {
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		setOnTouchListener(gestureListener);
	}

	public CustomListView(Context context) {
		super(context);
		initializeGestureRecognizer();
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeGestureRecognizer();
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeGestureRecognizer();
	}

	private void changeGalleryPosition(final boolean shouldIncrease) {
		
		int size = BookShelfActivity.getGallery().getCount();
		int currentPosition = BookShelfActivity.getGallery().getSelectedItemPosition();
		
		if(shouldIncrease) {
			if(currentPosition == size -1) return;
			currentPosition++;
			if(currentPosition >= size) {
				currentPosition = size -1;
			} 
						
		} else {
			if(currentPosition == 0) return;
			currentPosition--;
			if(currentPosition < 0) {
				currentPosition = 0;
			} 
		}
		
		final int newPosition = currentPosition;
		//Animation outAnimation = AnimationUtils.makeOutAnimation(getContext(), !shouldIncrease);
		int animId = shouldIncrease ? R.anim.slide_left : R.anim.slide_right;// android.R.anim.
		Animation outAnimation = AnimationUtils.loadAnimation(getContext(), animId);
		outAnimation.setAnimationListener(new AnimationListener() {

		
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			
			public void onAnimationEnd(Animation animation) {
				int animId = shouldIncrease ? R.anim.slide_left_out : R.anim.slide_right_out;
				//Animation inAnimation = AnimationUtils.makeInAnimation(getContext(), !shouldIncrease);
				Animation inAnimation = AnimationUtils.loadAnimation(getContext(), animId);
				BookShelfActivity.getGallery().setSelection(newPosition);		
				BookShelfActivity.getGallery().setAnimation(inAnimation);				
			}
		});
		
		BookShelfActivity.getGallery().startAnimation(outAnimation);

	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > MAX_Y_OFF)
					return false;
				if (e1.getX() - e2.getX() > MIN_LENGTH
						&& Math.abs(velocityX) > VELOCITY_LIMIT) {
					changeGalleryPosition(true);
					return true;
				} else if (e2.getX() - e1.getX() > MIN_LENGTH
						&& Math.abs(velocityX) > VELOCITY_LIMIT) {
					changeGalleryPosition(false);
					return true;
				}
			} catch (Exception e) {
			}
			return false;
		}
	}
}
