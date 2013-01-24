package com.chaturs;

import java.util.List;
import org.geometerplus.zlibrary.ui.android.R;
import com.chaturs.models.Book;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookShelfArrayAdapter extends ArrayAdapter {
	
	
	public Context context;
	public List<Book> temp;
	private int resourceId;
	
	
public BookShelfArrayAdapter(Context context, int textViewResourceId,List<Book> objects) {
	super(context, textViewResourceId, objects);
	
	this.context=context;
	this.temp=objects;
	this.resourceId = textViewResourceId;
	}


@Override
public int getCount() {
	return temp.size();
}

@Override
public int getPosition(Object item) {
	
	return temp.indexOf(item);
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	
	LayoutInflater inflater = LayoutInflater.from(context);
	View view =inflater.inflate(resourceId, null);
	
//	view.findViewById(R.id.BookTitleId).setLayoutParams(new LayoutParams(210, 80));
	
	Book b = temp.get(position);
	
	TextView Tv=(TextView)view.findViewById(R.id.Tittle);
	Tv.setText(b.getTitle());
	
	TextView author = (TextView) view.findViewById(R.id.Author);
	author.setText(b.getAuthor());
	
	ImageView Iv=(ImageView)view.findViewById(R.id.Image);
	Iv.setImageDrawable(Drawable.createFromPath(b.getThumbNailPath()));
	
	return view;
}

@Override
public Object getItem(int position) {
	return temp.get(position);
}

}
