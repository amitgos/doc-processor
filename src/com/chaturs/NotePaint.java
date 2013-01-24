package com.chaturs;


public class NotePaint {
	
	private String title;
	
	private String path;
	
	private long id;
	
	public NotePaint(){
		
	}

	public NotePaint(String tilte , String path ){
		
		this.id = 0;
 		this.title = tilte ;
		this.path = path ;
	}

	public NotePaint(long id ,String tilte , String path ){
		
		this.id = id;
 		this.title = tilte ;
		this.path = path ;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
