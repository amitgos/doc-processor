package com.chaturs.models;

import java.util.List;

public class Category {

	private String name;
	private List<Book> bookList;
	
	Category(String name, List<Book> bookList) {
		this.name = name;
		this.bookList = bookList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void setBookList(List<Book> bookList) {
		this.bookList = bookList;
	}
}
