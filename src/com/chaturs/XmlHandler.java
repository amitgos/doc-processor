package com.chaturs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.chaturs.models.Annotate;
import com.chaturs.models.DatabaseHandler;
import com.chaturs.models.Note;
import com.chaturs.models.Problem;
import com.chaturs.models.Solution;
import com.chaturs.models.StudyGroup;

public class XmlHandler extends DefaultHandler {

	private DatabaseHandler databaseHandler ;
	
	private boolean cuurentElement = false;
	private boolean in_annotation = false;
	private boolean in_note = false;
	private boolean in_id = false;
	private boolean in_solution = false;
	
	private StringBuffer currentValue ;
	private long  studygroup_id ;
	private String studyGroupName;
	
	private long note_id;
	private String note_title;
	private String note_content;
	private String note_user;
	private String note_time;
	
	private long problem_id;
	private String problem_name;
	private String problem_user;
	private String problem_time;
	
	private long annotate_id;
	private String annotate_comment;
	private String annotate_imagepath;
	
	private long solution_id;
	private String solution_reply;
	private String solution_user;
	
	
	private List<Note> noteList = new ArrayList<Note>();
	private List<Problem> problemList = new ArrayList<Problem>();
	private List<Annotate> annoationList = new ArrayList<Annotate>();
	private List<Solution> solutionList = new ArrayList<Solution>();
	private List<StudyGroup> studyGroupList = new ArrayList<StudyGroup>();
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if(cuurentElement){
			currentValue.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if(localName.equals("group")){
			studyGroupList.add(new StudyGroup(studygroup_id, studyGroupName, problemList, noteList));
			
		}else if(localName.equals("note")){
			in_note = false;
			noteList.add(new Note(0, note_id, studygroup_id, note_title, note_content,note_user,note_time));
			
		}else if(localName.equals("problem")){
			problemList.add(new Problem(problem_id, "", problem_name, studygroup_id,problem_user,problem_time,annoationList,solutionList));
		}else if(localName.equals("annotation")){
			in_annotation = false;
			annoationList.add(new Annotate(annotate_id, "", annotate_comment,"", annotate_imagepath));
		}else if(in_annotation && localName.equals("id")){
			cuurentElement = false;
			in_id = false;
			annotate_id = Long.valueOf( currentValue.toString() );
		}else if(in_note && localName.equals("id")){
			cuurentElement = false;
			in_id = false;
			note_id = Long.valueOf( currentValue.toString() );
		}else if(localName.equals("title")){
			cuurentElement = false;
			note_title = currentValue.toString();
			
		}else if(localName.equals("content")){
			cuurentElement = false;
			note_content = currentValue.toString();
			
		}else if(localName.equals("name")){
			cuurentElement = false;
			problem_name = currentValue.toString();
			
		}else if(localName.equals("comment")){
			cuurentElement = false;
			annotate_comment = currentValue.toString();
			
		}else if(localName.equals("image_path")){
			cuurentElement = false;
			annotate_imagepath = currentValue.toString();
		}else if(localName.equals("posted_by")){
			cuurentElement = false;
			note_user = currentValue.toString();
		}else if(localName.equals("solution")){
			in_solution = false;
			solutionList.add(new Solution(0, solution_id, problem_id, solution_reply, solution_user));
		}else if(localName.equals("reply")){
			cuurentElement = false;
			solution_reply = currentValue.toString();
		}else if(localName.equals("replied_by")){
			cuurentElement = false;
			solution_user = currentValue.toString();
		}else if(in_solution && localName.equals("id")){
			cuurentElement = false;
			in_id=false;
			solution_id = Long.valueOf( currentValue.toString() );
		}else if(localName.equals("posted_time")){
			cuurentElement = false;
			note_time = currentValue.toString();
		}
	}

	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		databaseHandler = DatabaseHandler.getInstance();
		
	}

	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if(localName.equals("group")){
			
			 noteList = new ArrayList<Note>();
			 problemList = new ArrayList<Problem>();
			
			
			studygroup_id = Long.valueOf(attributes.getValue("id"));
			studyGroupName = attributes.getValue("name");
				
		}else if(localName.equals("note")){
			in_note = true;
			
		}else if(localName.equals("problem")){
				
				problem_id = Long.valueOf(attributes.getValue("id"));
			
				problem_name  = attributes.getValue("name");
				problem_user = attributes.getValue("posted_by");
				problem_time = attributes.getValue("post_time");
				 annoationList = new ArrayList<Annotate>();
				 solutionList = new ArrayList<Solution>();
				
		}else if(localName.equals("annotation")){
			in_annotation = true;
			
		}else if(in_annotation && localName.equals("id")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(in_note && localName.equals("id")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(localName.equals("title")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(localName.equals("content")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(localName.equals("name")){
			cuurentElement = true;
			currentValue = new StringBuffer();
		}else if(localName.equals("comment")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(localName.equals("image_path")){
			cuurentElement = true;
			currentValue = new StringBuffer();
		}else if(localName.equals("posted_by")){
			cuurentElement = true;
			currentValue = new StringBuffer();
			
		}else if(localName.equals("solution")){
			in_solution = true;
		}else if(localName.equals("reply")){
			cuurentElement = true;
			currentValue = new StringBuffer();
		}else if(localName.equals("replied_by")){
			cuurentElement = true;
			currentValue = new StringBuffer();
		}else if(in_solution && localName.equals("id")){
			
			cuurentElement = true;
			currentValue = new StringBuffer();
		}else if(localName.equals("posted_time")){
			cuurentElement = true;
			currentValue = new StringBuffer();
		}
	}

	public boolean UpdateStudyGroupList() {
		
		for(int k=0;k < studyGroupList.size();k++){
			StudyGroup studyGroup = studyGroupList.get(k);
			databaseHandler.insertStudyGroup(studyGroup.getServerId(), studyGroup.getName());
			
			if(!studyGroup.getProblemList().isEmpty()){
				Log.i("Test","Inserting Problem");
				for(int i=0;i < studyGroup.getProblemList().size();i++){
					Problem problem = studyGroup.getProblemList().get(i);
					databaseHandler.insertProblem(problem);
					
				}
			}
			if(!studyGroup.getNoteList().isEmpty()){
				Log.i("Test","size of Note list for sg id " + studyGroup.getServerId() + " " + studyGroup.getNoteList().size());
				for(int i=0;i < studyGroup.getNoteList().size();i++){
					Note note = studyGroup.getNoteList().get(i);
					databaseHandler.insertNote(note);
				}
			}
		}
		return true;
	}
}
