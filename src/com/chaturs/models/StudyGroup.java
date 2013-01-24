package com.chaturs.models;

import java.util.List;

public class StudyGroup {

	private long serverId;
	private String name;
	private List<Problem> problemList;
	private List<Note> noteList;
	

	public StudyGroup(long serverId,String name, List<Problem> problemList,List<Note> noteList) {
		this.serverId = serverId ;
		this.name = name;
		this.problemList = problemList;
		this.noteList = noteList;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Problem> getProblemList() {
		return problemList;
	}

	public void setProblemList(List<Problem> problemList) {
		this.problemList = problemList;
	}
	
	public long getServerId() {
		return serverId;
	}

	public void setServerId(long serverId) {
		this.serverId = serverId;
	}
	public List<Note> getNoteList() {
		return noteList;
	}
	public void setNoteList(List<Note> noteList) {
		this.noteList = noteList;
	}

}
