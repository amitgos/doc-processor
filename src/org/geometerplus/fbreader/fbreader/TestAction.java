package org.geometerplus.fbreader.fbreader;

import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.android.fbreader.TestActivity;
import org.geometerplus.zlibrary.ui.android.dialogs.ZLAndroidDialogManager;

public class TestAction extends FBAction {

	TestAction(FBReader fbreader) {
		super(fbreader);
	}

	@Override
	protected void run() {

		final ZLAndroidDialogManager dialogManager =
			(ZLAndroidDialogManager)ZLAndroidDialogManager.Instance();
		dialogManager.runActivity(TestActivity.class);
	}
}
