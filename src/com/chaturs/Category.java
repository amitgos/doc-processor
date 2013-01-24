package com.chaturs;

import java.util.List;

import com.chaturs.models.Book;

public class Category {
	
	
	public Category(String Name , List<Book> books){
		
		this.Name = Name;
		this.books = books;
	}
	
	
	private String Name;
	private List<Book> books;
	
	
	
	public String getName(){
		
		return this.Name;
	}
	
	public List<Book> getListOfBooks(){
		
		return this.books;
	}

}
