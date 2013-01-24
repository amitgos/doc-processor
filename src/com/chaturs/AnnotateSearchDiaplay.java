package com.chaturs;

import java.util.ArrayList;
import java.util.List;
import org.geometerplus.zlibrary.ui.android.R;
import com.chaturs.models.Annotate;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class AnnotateSearchDiaplay extends Activity {
	private TextView tv , title;
	private ImageView iv;
	private Annotate annotate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("AnnoateList Search");
		setContentView(R.layout.annotate);
		annotate = AnnotateSearchActivity.annotate;
		
		title = (TextView)findViewById(R.id.annotation_title);
		 tv = (TextView) findViewById(R.id.annotatedText);
		 iv = (ImageView)findViewById(R.id.annotatedImage);
		  String text = annotate.getText();
		  title.setText("Annotation");
		 tv.setText(text);
		 iv.setImageDrawable(Drawable.createFromPath(annotate.getLocalImagePath()));
	}
}
