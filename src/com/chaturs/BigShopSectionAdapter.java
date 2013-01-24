package com.chaturs;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BigShopSectionAdapter extends ArrayAdapter {
	
	
	public Context context;
	public List<String> temp;
	
	int studyGroup_drawables[] = {R.drawable.bigshop_free,R.drawable.bigshop_new,
			R.drawable.bigshop_mostpopular,R.drawable.bigshop_suggestedforu};
	
public BigShopSectionAdapter(Context context, int textViewResourceId,List<String> objects) {
	super(context, textViewResourceId, objects);
	this.context=context;
	this.temp=objects;
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
	View view =inflater.inflate(R.layout.list, null);
	
	TextView Tv=(TextView)view.findViewById(R.id.Text);
	Tv.setText(temp.get(position));
	
	ImageView Iv=(ImageView)view.findViewById(R.id.Image);
	Iv.setBackgroundResource(studyGroup_drawables[position%4]);
	return view;
}

@Override
public Object getItem(int position) {
	return temp.get(position);
}

}
