package com.chaturs.models;

public class Solution {
	
	private long id;
	private long serverId;
	private long problemId;
	private String solution;
	private String user;
	
	public Solution(){
		this.solution = "";
		this.user = "";
	}
	
	public Solution(long id ,long serverId,long problemId, String solution , String user){
		
		this.id = id;
		this.serverId = serverId;
		this.problemId = problemId ;
		this.solution = solution;
		this.user = user;
	}

	
	public long getId() {
		return id;
	}

	
	public void setId(long id) {
		this.id = id;
	}

	
	public long getServerId() {
		return serverId;
	}

	
	public void setServerId(long serverId) {
		this.serverId = serverId;
	}

	
	public long getProblemId() {
		return problemId;
	}

	public void setProblemId(long problemId) {
		this.problemId = problemId;
	}

	
	public String getSolution() {
		return solution;
	}

	
	public void setSolution(String solution) {
		this.solution = solution;
	}

	
	public String getUser() {
		return user;
	}

	
	public void setUser(String user) {
		this.user = user;
	}
	
}
