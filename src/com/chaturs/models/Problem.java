package com.chaturs.models;

import java.util.ArrayList;
import java.util.List;

public class Problem {

	private String problemId;
	private String name;
	private String studyGroup;
	private String object;
	private long serverId;
	private long studygroup_server_id;
	private List<Annotate> annotateList = new ArrayList<Annotate>();	
	private List<Solution> solutionList = new ArrayList<Solution>();
	private String selectedProblemIds;
	private String userName;
	private String time;
	
	public Problem(long serverId , String problemId, String name, long studygroupServerId,String userName,String time,List<Annotate> annotateList,List<Solution> solutionList) {
		
		this.problemId = problemId != null ?  problemId : "";
		this.name = name != null ? name : "" ;
		this.studygroup_server_id = studygroupServerId;
		this.object = "annotate";
		this.serverId = serverId ;
		this.annotateList = annotateList ;
		this.solutionList = solutionList;
		this.userName = userName;
		this.time = time;
		
	}
	
	public String getObject() {
		return object;
	}

	
	public void setObject(String object) {
		this.object = object;
	}
	
	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	public String getStudyGroup() {
		return studyGroup;
	}

	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public long getStudygroupServerId() {
		return studygroup_server_id;
	}

	
	public void setStudygroupServerId(long studygroupServerId) {
		this.studygroup_server_id = studygroupServerId;
	}
	public long getServerId() {
		return serverId;
	}

	public void setServerId(long serverId) {
		this.serverId = serverId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Annotate> getAnnotateList() {
		return annotateList;
	}

	
	public List<Solution> getSolutionList() {
		return solutionList;
	}

	
	public void setSolutionList(List<Solution> solutionList) {
		this.solutionList = solutionList;
	}

	
	public String getTime() {
		return time;
	}

	
	public void setTime(String time) {
		this.time = time;
	}

}
