package com.chaturs;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.MyBookreaderActivity;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.chaturs.models.Book;
import com.chaturs.models.Category;
import com.chaturs.models.DatabaseHandler;

public class BookShelfActivity extends Activity {

	private ListView list;
	private CustomArrayAdapter adapter;
	private static Gallery gallery;

	private TextView text;
	private static final int SETTINGS = 1;
	private static final int SEARCH = 2;
	List<Book> array = new ArrayList<Book>();
	List<List> arrayList = new ArrayList<List>();
	List<String> category = new ArrayList<String>();
	List<Category> lCategory = new ArrayList<Category>();
	private int Orientation;
	
	private DatabaseHandler database;
	private BookShelfActivity instance;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.book_shelf);
		Intent intent = getIntent();
		if(Intent.ACTION_VIEW.equals(intent.getAction())){
		Orientation = getWindowManager().getDefaultDisplay().getOrientation();
		
		Log.i("Orientation",Integer.toString(getWindowManager().getDefaultDisplay().getOrientation()));
		
		dialog = ProgressDialog.show(this, "", 
                "Loading...", true);

		Thread thread = new Thread(new Runnable() {

			public void run() {
				database = DatabaseHandler.getInstance();
				handler.sendEmptyMessage(0);
			}
		});
	
		thread.start();
		}
	}
	
	final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			lCategory = database.listCategories();
			gallery = (Gallery) findViewById(R.id.gallery);
			gallery.setAdapter(new galleryAdapter(instance));
			dialog.dismiss();
		}
	};

	public static Gallery getGallery() {
		return gallery;
	}

	@Override
	protected void onDestroy() {
		gallery = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, SETTINGS, 0, "Settings")
		.setIcon(R.drawable.ic_menu_preferences);
		
		menu.add(0,SEARCH,0,"Search")
		.setIcon(android.R.drawable.ic_search_category_default);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case SETTINGS:
			break;
		case SEARCH :
			onSearchRequested();
		}

		return true;
	}

	public class galleryAdapter extends BaseAdapter {

		Context mContext;
		int pos;
		int count;

		public galleryAdapter(Context context) {

			mContext = context;
		}

		public int getCount() {
			
			
			return lCategory.size();
		}

		public Object getItem(int arg0) {
			return lCategory.get( arg0);
		}

		public long getItemId(int arg0) {
			return  arg0;
		}

		public View getView(int position, View arg1, ViewGroup arg2) {
			
			int resourceId;
			pos = position;
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.scroll_list, null);

			TextView text = (TextView) view.findViewById(R.id.Text);
			ImageButton left = (ImageButton) view.findViewById(R.id.left);
			ImageButton right = (ImageButton) view.findViewById(R.id.right);
			
//			resourceId = Orientation == 0 ? R.layout.book_display : R.layout.book_display2;
			resourceId = R.layout.book_display ;
			
			ListView list = (ListView) view.findViewById(R.id.list_1);
			list.setAdapter(new BookShelfArrayAdapter(mContext,resourceId,
					lCategory.get(position).getBookList()));
			list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
//					Toast toast = Toast.makeText(getApplicationContext(),
//							lCategory.get(pos).getBookList().get(position)
//									.getTitle(), Toast.LENGTH_SHORT);
//					toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
//							0, 0);
//					toast.show();
					String Path = lCategory.get(pos).getBookList().get(position).getFilePath();
//					Intent intent = new Intent(mContext, MyBookreaderActivity.class);
//					intent.putExtra("Book_Path", Path);
//					startActivity(intent);
					
					Uri.Builder uriBuilder = new Uri.Builder();
					uriBuilder.path(Path);
					Uri uri =uriBuilder.build();
					
					Intent intent = new Intent(mContext,MyBookreaderActivity.class);
					intent.putExtra("book_path", Path);
					startActivity(intent);
//					if(uri != null){
//					intent.setData(uri);
//					intent.setAction(Intent.ACTION_VIEW);
//					startActivity(intent);
//					}

				}
			});

			text.setText(lCategory.get(position).getName().toString());
			
			int leftId = pos!=0 ? R.drawable.left_button_background : R.drawable.left_arrow_fade;
			int rightId = pos!= (lCategory.size()-1) ? R.drawable.right_button_background : R.drawable.right_arrow_fade;
			left.setBackgroundResource(leftId);
			right.setBackgroundResource(rightId);
			
			left.setOnClickListener(new ImageButton.OnClickListener(){

				public void onClick(View v) {
					if(pos!=0){
					gallery.setSelection(--pos);
					}
				}
			});
			
			right.setOnClickListener(new ImageButton.OnClickListener(){

				public void onClick(View v) {
					if(pos != (lCategory.size()-1)){
					gallery.setSelection(++pos);
					}
				}
			});
			
			return view;
		}
	}

	
	@Override
	protected void onStop() {
		super.onStop();
		
	ZLAndroidWidget.CROP = false;
	}	
}