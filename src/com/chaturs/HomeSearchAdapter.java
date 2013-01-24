package com.chaturs;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Book;
import com.chaturs.models.SearchObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeSearchAdapter extends ArrayAdapter {

	public Context context;
	public List<SearchObject> temp;
	private int resourceId;
	
	
public HomeSearchAdapter(Context context, int textViewResourceId,List<SearchObject> objects) {
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
	
	
	if(convertView == null){
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView =inflater.inflate(R.layout.problem_search_display, null);
	}
	
	
	
	SearchObject searchObject = temp.get(position);
	
	TextView title =(TextView)convertView.findViewById(R.id.title);
	title.setText(searchObject.getTitle());
	
	TextView subTitle  = (TextView) convertView.findViewById(R.id.subTitle);
	TextView fromUser = (TextView) convertView.findViewById(R.id.fromUser);
	subTitle.setText(searchObject.getSubTitle());
	fromUser.setText(searchObject.getUser());
//	if("Book".equalsIgnoreCase(searchObject.getObjectType())){
//	ImageView Iv=(ImageView)view.findViewById(R.id.searchObjectImage);
//	Iv.setImageDrawable(Drawable.createFromPath(searchObject.getmBook().getThumbNailPath()));
//	}else if ("Problem".equalsIgnoreCase(searchObject.getObjectType())){
//		ImageView Iv=(ImageView)view.findViewById(R.id.searchObjectImage);
////		Iv.setBackgroundResource(R.drawable.studygroup_icon);
//	}
	
	return convertView;
}

@Override
public Object getItem(int position) {
	return temp.get(position);
}
}
