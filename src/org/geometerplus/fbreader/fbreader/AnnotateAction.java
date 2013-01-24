package org.geometerplus.fbreader.fbreader;

import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.android.fbreader.TOCActivity;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.ui.android.dialogs.ZLAndroidDialogManager;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

import android.content.Intent;

public class AnnotateAction extends FBAction {

	AnnotateAction(FBReader fbreader) {
		super(fbreader);
		
	}

	@Override
	public boolean isVisible() {
		
		return !AnnotateActivity.isEmpty();
	}

	@Override
	protected void run() {
		final ZLAndroidDialogManager dialogManager =
			(ZLAndroidDialogManager)ZLAndroidDialogManager.Instance();
		dialogManager.runActivity(AnnotateActivity.class);
	}
}
