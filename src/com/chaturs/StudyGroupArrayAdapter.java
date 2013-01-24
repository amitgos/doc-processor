package com.chaturs;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Problem;
import com.chaturs.models.StudyGroup;

public class StudyGroupArrayAdapter extends ArrayAdapter<String>{

	private StudyGroup studygroup;
	private Context context;
	private int resource ;
	
	public StudyGroupArrayAdapter(Context context, int resource,
			StudyGroup studygroup) {
		super(context, resource);
		this.context = context;
		this.studygroup = studygroup ;
		this.resource = resource;
	}

	@Override
	public int getCount() {
		int count = 0;
		if(studygroup.getProblemList() != null && !studygroup.getProblemList().isEmpty()){
			count = studygroup.getProblemList().size();
		}
		
		if(studygroup.getNoteList()!= null && !studygroup.getNoteList().isEmpty()){
			count +=  studygroup.getNoteList().size();
		}
		return count;
	}

	@Override
	public String getItem(int position) {
		
		return studygroup.getProblemList().get(position).getName();
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(resource, null);
		}
		
		TextView titleView  = (TextView) convertView.findViewById(R.id.tittle);
		TextView subTitle = (TextView) convertView.findViewById(R.id.subTittle);
		TextView userName = (TextView) convertView.findViewById(R.id.fromUser);
		TextView timeView =(TextView) convertView.findViewById(R.id.time);
		int size = studygroup.getProblemList().size();
		if(position < size ){
		String text = studygroup.getProblemList().get(position).getName();
		String object = studygroup.getProblemList().get(position).getObject();
		String user = studygroup.getProblemList().get(position).getUserName();
		String time = studygroup.getProblemList().get(position).getTime();
		if(object.equals("annotate")&& null == text || "".equalsIgnoreCase(text)){
			text = DatabaseHandler.getInstance().getFirstAnnotateText(studygroup.getProblemList().get(position).getProblemId());
			if(null == text || "".equalsIgnoreCase(text)) {
				text = "Unknown";
			}
		}
		
		titleView.setText(text);
		subTitle.setText("annotate");
		userName.setText(user);
		timeView.setText(time);
		convertView.setTag(position);
		}else{
			String text = studygroup.getNoteList().get(Math.abs(position-size)).getTitle();
			String user = studygroup.getNoteList().get(Math.abs(position-size)).getUserName();
			String time = studygroup.getNoteList().get(Math.abs(position-size)).getTime();
			titleView.setText(text);
			subTitle.setText("Note");
			userName.setText(user);
			timeView.setText(time);
			convertView.setTag(position);
		}
		return convertView;
	}
}
