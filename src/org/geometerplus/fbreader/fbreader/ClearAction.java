package org.geometerplus.fbreader.fbreader;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

public class ClearAction extends FBAction{

	ClearAction(FBReader fbreader) {
		super(fbreader);
	}
	
	public boolean isVisible() {
		
		return ZLAndroidWidget.CROP;
}

	@Override
	protected void run() {
		
		ZLAndroidWidget.CROP = !ZLAndroidWidget.CROP;
		
	}

}
