package com.chaturs;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Book;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BigShopArrayAdapter extends ArrayAdapter {

	public Context context;
	public List<String> temp;
	private int Icon;
	private LayoutInflater inflater;
	
public BigShopArrayAdapter(Context context, int textViewResourceId,List<String> objects) {
	super(context, textViewResourceId, objects);
	
	this.context=context;
	this.temp=objects;
	this.Icon = textViewResourceId;
	inflater = LayoutInflater.from(context);
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
	ViewHolder holder;
	
	if(convertView == null){
	convertView =inflater.inflate(R.layout.big_shop_list, null);
	holder = new ViewHolder();
	holder.Tv = (TextView)convertView.findViewById(R.id.Text);
	holder.Iv = (ImageView)convertView.findViewById(R.id.Image);
	convertView.setTag(holder);
	}else{
		holder = (ViewHolder) convertView.getTag();
	}
	
	holder.Tv.setText(temp.get(position).toString());
	holder.Iv.setBackgroundResource(Icon);
	
	return convertView;
}

 static class ViewHolder{
	 
	 TextView Tv;
	 ImageView Iv;
 }

@Override
public Object getItem(int position) {
	return temp.get(position);
}

}
