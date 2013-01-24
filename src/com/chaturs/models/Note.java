package com.chaturs.models;

public class Note {

	private long _id;
	private String title;
	private String content;
	private long serverId;
	private long studygroupId;
	private String selectedStudyGroupIds;
	private String userName;
	private String time;
	
	public Note(long _id , long serverId, long studygroupid,String title , String content,String userName , String time){
		
		this._id = _id;
		this.serverId = serverId ;
		
		this.title = title ;
		this.content = content;
		this.studygroupId = studygroupid;
		this.userName = userName;
		this.time = time;
	}
	public Note(String selectedstudyGroupIds,String title , String content,String userName,String time){
		
		this.title = title ;
		this.content = content;
		this.selectedStudyGroupIds = selectedstudyGroupIds;
		this.userName = userName;
		this.time = time;
	}

	public long getServerId() {
		return serverId;
	}
	public void setServerId(long serverId) {
		this.serverId = serverId;
	}

	public long get_id() {
		return _id;
	}
	
	public void set_id(long id) {
		this._id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setNote(String content) {
		this.content = content;
	}
	
	public long getStudygroupId() {
		return studygroupId;
	}
	public void setStudygroupId(long studygroupId) {
		this.studygroupId = studygroupId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getSelectedStudyGroupIds() {
		return selectedStudyGroupIds;
	}

	public void setSelectedStudyGroupIds(String selectedStudyGroupIds) {
		this.selectedStudyGroupIds = selectedStudyGroupIds;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}
		
