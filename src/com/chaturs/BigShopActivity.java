package com.chaturs;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.MyBookreaderActivity;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.R.string;

import com.chaturs.BookShelfActivity.galleryAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class BigShopActivity extends Activity {

	private ListView listView;
	private List<List<String>> list = new ArrayList<List<String>>();
	private List<String> array1 = new ArrayList<String>();
	private List<String> array2 = new ArrayList<String>();
	private List<String> array3 = new ArrayList<String>();
	private List<String> array4 = new ArrayList<String>();
	private int Orientation, resourceId;
	private View galleryView;
	private static Gallery gallery;
	private List<String> Catagories = new ArrayList<String>();
	private List<Integer> resource = new ArrayList<Integer>();
	private ArrayList<Integer> icons = new ArrayList<Integer>();
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Orientation = getWindowManager().getDefaultDisplay().getOrientation();

		setContentView(R.layout.big_shop);
		
		
		resource.add(R.layout.bigshop_tutorial_purchase_page);
		resource.add(R.layout.bigshop_book_purchase_page);
		resource.add(R.layout.bigshop_chapter_purchase_page);
		resource.add(R.layout.bigshop_notes_purchase_page);
		
	
		icons.add(R.drawable.tutorials);
		icons.add(R.drawable.books);
		icons.add(R.drawable.chapter);
		icons.add(R.drawable.notes);

		Catagories.add("Tutorial");
		Catagories.add("Books");
		Catagories.add("Chapters");
		Catagories.add("Notes");
		
		
		
		array1.add("Tutorial1");
		array1.add("Tutorial2");
		array1.add("Tutorial3");
		array1.add("Tutorial4");
		array1.add("Tutorial5");

		array2.add("Book1");
		array2.add("Book2");
		array2.add("Book3");
		array2.add("Book4");
		array2.add("Book5");
		
		array3.add("Chapter1");
		array3.add("Chapter2");
		array3.add("Chapter3");
		array3.add("Chapter4");
		array3.add("Chapter5");

		array4.add("Note1");
		array4.add("Note2");
		array4.add("Note3");
		array4.add("Note4");
		array4.add("Note5");

		list.add(array1);
		list.add(array2);
		list.add(array3);
		list.add(array4);
		
		gallery = (Gallery) findViewById(R.id.big_shop_gallery);
		gallery.setAdapter(new galleryAdapter(this));
		
	}
	
	public static Gallery getGallery() {
		return gallery;
	}
	
	public class galleryAdapter extends BaseAdapter {

		Context mContext;
		int pos;
		int count;

		public galleryAdapter(Context context) {

			mContext = context;
		}

		public int getCount() {
			
			
			return list.size();
		}

		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		public long getItemId(int arg0) {
			return  arg0;
		}

		public View getView(int position, View arg1, ViewGroup arg2) {
			
			int resourceId;
			pos = position;
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.big_shop_scroll_list, null);

			TextView text = (TextView) view.findViewById(R.id.big_shop_catagory);
			ImageButton left = (ImageButton) view.findViewById(R.id.big_shop_left);
			ImageButton right = (ImageButton) view.findViewById(R.id.big_shop_right);
			
			ListView listView = (ListView) view.findViewById(R.id.big_shop_list);
			listView.setAdapter(new BigShopArrayAdapter(mContext,icons.get(pos),
					list.get(pos)));
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
						
					Intent intent = new Intent(mContext ,BigShopBookPurchaseActivity.class);
					int res = resource.get(pos);
					intent.putExtra("resource", res);
					startActivity(intent);
				}
			});

			text.setText(Catagories.get(position).toString());
			
			int leftId = pos!=0 ? R.drawable.left_button_background : R.drawable.left_arrow_fade;
			int rightId = pos!= (Catagories.size()-1) ? R.drawable.right_button_background : R.drawable.right_arrow_fade;
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
					if(pos != (Catagories.size()-1)){
					gallery.setSelection(++pos);
					}
				}
			});
			
			return view;
		}
	}

	private static final int SETTINGS = Menu.FIRST;
	private static final int SEARCH = Menu.FIRST +1;
	
	  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		  
		  menu.add(0, SETTINGS, 0, "Settings")
		  .setIcon(R.drawable.ic_menu_preferences);
		  menu.add(0, SEARCH, 0, "Search")
		  .setIcon(android.R.drawable.ic_search_category_default);
		
		return true;
	}
	  
	  @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  
		  switch(item.getItemId()){

		  case SETTINGS:
			  
			  break;
			
		  case SEARCH:
			  onSearchRequested();
			  break;
		  
		  }
		return true;
	}
	
}
