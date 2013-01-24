package com.chaturs;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;


public class StudyGroupSearchAdapter extends ArrayAdapter {


	private List<Problem> problems;
	private List<Note> notes;
	private Context context;
	DatabaseHandler databaseHandler;
	
	public StudyGroupSearchAdapter(Context context, int resource,
			List<Problem> problems,List<Note> notes) {
		super(context, resource);
		this.context = context;
		this.problems = problems ;
		this.notes = notes;
		databaseHandler = DatabaseHandler.getInstance();
	}

	@Override
	public int getCount() {
		
		int count =0;
		if(problems != null && !problems.isEmpty()){
			count += problems.size();
		}
		
		if(notes !=null && !notes.isEmpty()){
			count += notes.size();
		}
		return count;
	}

	@Override
	public String getItem(int position) {
		int len = problems.size();
		if(position < problems.size()){
			return problems.get(position).getName();
		}else{
			len = len - position;
			return problems.get(Math.abs(len)).getName();
			
		}
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.problem_search_display, null);
		}
		
		TextView textView = (TextView) convertView.findViewById(R.id.title);
		TextView subTitle = (TextView) convertView.findViewById(R.id.subTitle);
		TextView userName = (TextView) convertView.findViewById(R.id.fromUser);
		
		int size = problems.size();
		if(position < size ){
		String text = problems.get(position).getName();
		String object = problems.get(position).getObject();
		String user = problems.get(position).getUserName();
		if(object.equals("annotate")&& null == text || "".equalsIgnoreCase(text)){
			text = DatabaseHandler.getInstance().getFirstAnnotateText(problems.get(position).getProblemId());
			if(null == text || "".equalsIgnoreCase(text)) {
				text = "Unknown";
			}
		}
		
		textView.setText(text);
		subTitle.setText("annotate");
		userName.setText(user);
		convertView.setTag(position);
		}else{
			String text = notes.get(Math.abs(position-size)).getTitle();
			String user = notes.get(Math.abs(position-size)).getUserName();
			textView.setText(text);
			subTitle.setText("Note");
			userName.setText(user);
			convertView.setTag(position);
		}
		return convertView;
	}
	
}
