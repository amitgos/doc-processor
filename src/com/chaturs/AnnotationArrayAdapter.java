package com.chaturs;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Problem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AnnotationArrayAdapter extends ArrayAdapter {
	private List<Annotate> list;
	private Context context;
	
	public AnnotationArrayAdapter(Context context, int resource,
			List<Annotate> objects) {
		super(context, resource);
		this.context = context;
		list = objects ;
	}

	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public String getItem(int position) {
		
		return list.get(position).getText();
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.problem_search_display,null);

		TextView AnnoateComment = (TextView) view.findViewById(R.id.title);
		TextView studyGroupName = (TextView) view.findViewById(R.id.subTitle);
		
		String text = list.get(position).getText();
//		if(null == text || "".equalsIgnoreCase(text)){
//			text = DatabaseHandler.getInstance().getFirstAnnotateText(list.get(position).getProblemId());
//			if(null == text || "".equalsIgnoreCase(text)) {
//				text = "Unknown";
//			}
//		}
		
		AnnoateComment.setText(text);
		
		return view;
	}


}
