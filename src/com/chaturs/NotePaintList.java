package com.chaturs;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;
import com.chaturs.models.DatabaseHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NotePaintList extends ListActivity implements OnItemClickListener {
	
	public static List<NotePaint> notePaintList = new ArrayList<NotePaint>();
	private static final int MENU_ID_RENAME = 1;
	 private static final int MENU_ID_REMOVE = 2;
	  private static final int DIALOG_RENAME_PAINT = 1;
	 private static final int ADD_NOTE_MENU_ID = Menu.FIRST ;
	 private static final String NOTE_PAINT_PATH  = Environment.getExternalStorageDirectory() + "/PaintImages/";
	 
	private NotePaint mCurrentRenameNotePaint;
	private EditText mInput;
	private Dialog mRenameDialog;
	private int curPos;
	
	public static List<NotePaint> getPaintList(){
		
		return notePaintList;
	}
	
	public static void removePaint(){
		
		if(!notePaintList.isEmpty()){
			notePaintList.remove(notePaintList.size()-1);
		}
	}
	
	public static void removePaint(int index) {
		if(notePaintList.size() > index) {
			notePaintList.remove(index);
		}
	}
	
	public static NotePaint getPaint(int position){
		
		 return notePaintList.isEmpty() ? null : notePaintList.get(position); 
	}
	
	public static int getIndex(){
		
		return  notePaintList.isEmpty()? 0 : notePaintList.size()-1;
	}
	public static boolean isEmpty(){
		
	return	notePaintList.isEmpty() ? true : false;
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle("Paint Notes");
		Log.i("Testing....","List Activity");
		
		setContentView(R.layout.notepaint_list);
		
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		updateAdapter();
		
	}


	private class NotePaintAdapter extends ArrayAdapter<NotePaint>{
		
		private  final LayoutInflater mInflater;
		private Context mContext ;
		
		public NotePaintAdapter(Context context) {
			super(context, 0);
			
			mContext = context ;
			 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			
			return notePaintList.size();
		}
		
		@Override
		public NotePaint getItem(int position) {
			
			return notePaintList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
                convertView = mInflater.inflate(R.layout.notepaint_item, parent, false);
            }
			 final TextView text = (TextView) convertView;
//			ImageView image =  (ImageView) view.findViewById(R.id.stylus_paint_id);
			
			text.setText(notePaintList.get(position).getTitle());
			
			return convertView;
		}
	}
	
	private void updateAdapter(){
		
		DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
		notePaintList = databaseHandler.listNotePaints();
		setListAdapter(new NotePaintAdapter(this));
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, ADD_NOTE_MENU_ID, 0, "Add Note")
		.setIcon(android.R.drawable.ic_menu_add);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case ADD_NOTE_MENU_ID:
			Intent intent = new Intent(getApplicationContext(),CreateNotePaint.class);
			intent.setAction(Intent.ACTION_INSERT);
			startActivity(intent);
			
			return true;
			

		default:
			break;
			
		}
		
		return super.onOptionsItemSelected(item);
	}


	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent intent = new Intent(this, CreateNotePaint.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra("Position", arg2);
		Log.i("testing Position", String.valueOf(arg2));
		startActivity(intent);
	}
	
	////  Context Menu
	
	
	private void rename(int pos){
		curPos = pos ;
		showDialog(DIALOG_RENAME_PAINT);
	}
	
 private boolean checkIfFileExists(String path){
    	 
    	 File file = new File(path);
    	 if(file.exists()){
//    		 Log.i("FILE","File exists");
    		 return true;
    	 }
//    	 Log.i("FILE","File Not exists");
    	 return false;
     }
 
	private void deletePaint(NotePaint notepaint){
		
		String path = notepaint.getPath();
		if(checkIfFileExists(path)){
		File file = new File(path);
		file.delete();
		}
		
		DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
		
		databaseHandler.deletePaint(notepaint);
		Toast.makeText(this,notepaint.getTitle()+ " " + "deleted",Toast.LENGTH_SHORT).show();
		updateAdapter();
	}
	
	private void cleanupRenameDialog(){
		
//		  if (mRenameDialog != null) {
//	            mRenameDialog.dismiss();
//	            mRenameDialog = null;
//	        }
//	        mCurrentRenameNotePaint = null;
		
	}
	
	private void changePaintName(){
		
		 String name = mInput.getText().toString();
		 DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
		 if(!TextUtils.isEmpty(name.trim())){
			 
			NotePaintList.getPaint(curPos).setTitle(name);
			databaseHandler.updatePaint(NotePaintList.getPaint(curPos));
				Toast.makeText(this,"Rename Done",Toast.LENGTH_SHORT).show();
		 }
		mCurrentRenameNotePaint = null;
		updateAdapter();
	}
	
	
	
	   @Override
	    protected Dialog onCreateDialog(int id) {
	        if (id == DIALOG_RENAME_PAINT) {
	            return createRenameDialog();
	        }
	        return super.onCreateDialog(id);
	    }

	    @Override
	    protected void onPrepareDialog(int id, Dialog dialog) {
	        super.onPrepareDialog(id, dialog);
	        if (id == DIALOG_RENAME_PAINT) {
	            mInput.setText(NotePaintList.getPaint(curPos).getTitle());
	        }
	    }

	    private Dialog createRenameDialog() {
	    	
	        final View layout = View.inflate(this, R.layout.dialog_rename, null);
	        mInput = (EditText) layout.findViewById(R.id.name);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setIcon(0);
	        builder.setTitle(getString(R.string.rename_paint_dialog_title));
	        builder.setCancelable(true);
	        builder.setOnCancelListener(new Dialog.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                cleanupRenameDialog();
	            }
	        });
	        builder.setNegativeButton(getString(R.string.cancel_action),
	            new Dialog.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    cleanupRenameDialog();
	                }
	            }
	        );
	        builder.setPositiveButton(getString(R.string.rename_action),
	            new Dialog.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    changePaintName();
	                }
	            }
	        );
	        builder.setView(layout);
	        return builder.create();
	    }
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
	
		 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	        menu.setHeaderTitle(((TextView) info.targetView).getText());
		
	        menu.add(0, MENU_ID_RENAME, 0, "Rename");
	        menu.add(0, MENU_ID_REMOVE, 0, "Delete");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		   final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)
           item.getMenuInfo();
		   

   switch (item.getItemId()) {
       case MENU_ID_RENAME:
    	   rename(menuInfo.position);
           return true;
       case MENU_ID_REMOVE:
    	   deletePaint(NotePaintList.getPaint(menuInfo.position));
           return true;
   }
		return super.onContextItemSelected(item);
	}
	
}
