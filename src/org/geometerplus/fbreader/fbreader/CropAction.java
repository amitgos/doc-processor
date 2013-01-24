package org.geometerplus.fbreader.fbreader;

import org.geometerplus.zlibrary.core.application.ZLApplication.ZLAction;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import android.util.Log;

public class CropAction extends FBAction{
	
	CropAction(FBReader fbreader) {
		super(fbreader);	
		
	}

	
	public boolean isVisible() {
		
			return !ZLAndroidWidget.CROP;
	}
	
	@Override
	protected void run() {
		
		ZLAndroidWidget.CROP = !ZLAndroidWidget.CROP;
	}
}
