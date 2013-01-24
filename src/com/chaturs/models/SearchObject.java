package com.chaturs.models;

public class SearchObject {
	
	private String objectType;
	private String title;
	private String subTitle ;
	private Book book;
	private Problem problem;
	private Note note;
	private String user ;
	
	
	

	public SearchObject(String title , String subTitle,String user, String objectType,Book book , Problem problem,Note note){
		
		this.title = title ;
		this.subTitle = subTitle ;
		this.user = user;
		this.objectType = objectType ;
		this.book = book;
		this.problem = problem;
		this.note = note;
	}
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}
	
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	
	public Book getmBook() {
		return book;
	}
	
	public void setmBook(Book book) {
		this.book = book;
	}
	public Problem getmProblem() {
		return problem;
	}
	
	public void setmProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Note getNote() {
		return note;
	}
	
	public void setNote(Note note) {
		this.note = note;
	}
	
	public String getUser() {
		return user;
	}

	
	public void setUser(String user) {
		this.user = user;
	}
	
}
