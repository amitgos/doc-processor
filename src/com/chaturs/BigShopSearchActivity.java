package com.chaturs;

import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class BigShopSearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("BigShopSearch");
		setContentView(R.layout.bookshelf_search);
		Intent intent = getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			
			String query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(this, String.valueOf(query), Toast.LENGTH_SHORT).show();
		}
	}
}
