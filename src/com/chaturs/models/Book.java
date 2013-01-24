package com.chaturs.models;

public class Book {

	private String id;
	private String title;
	private String author;
	private String category;
	private String filePath;
	private String thumbNailPath;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getThumbNailPath() {
		return thumbNailPath;
	}

	public void setThumbNailPath(String thumbNailPath) {
		this.thumbNailPath = thumbNailPath;
	}

	public Book()  {
		 initialize("","","","","","");
	}
	
	public Book(String bookId, String title, String author, String category, String filePath, String thumbNailPath) {
		initialize(bookId, title, author, category, filePath, thumbNailPath);
	}
	
	private void initialize(String bookId, String title, String author, String category, String filePath, String thumbNailPath) {
		
		this.id = bookId;
		this.title = title;
		this.author = author;
		this.category = category;
		this.filePath = filePath;
		this.thumbNailPath = thumbNailPath;
	
	}
}
