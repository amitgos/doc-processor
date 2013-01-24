package com.chaturs;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import org.geometerplus.zlibrary.ui.android.R;
public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
}
