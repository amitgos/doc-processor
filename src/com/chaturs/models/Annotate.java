package com.chaturs.models;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Annotate {
	
	private String text;
	private String localImagePath;
	private RectF rectF;
	private String annotateId;
	private long serverId;
	private String serverImagePath;
	
	public Annotate(){
		
	}
	
	public Annotate(long serverId,String annotateId, String text, String localImagePath,String serverImagePath) {
		this.serverId = serverId;
		this.annotateId = annotateId;
		this.text = text;
		this.localImagePath = localImagePath;
		this.serverImagePath = serverImagePath;
	}
	public void setText(String text){
		this.text = text;
	}
	
	public String getAnnotateId() {
		return annotateId;
	}

	public void setAnnotateId(String annotateId) {
		annotateId = annotateId;
	}

	public void setLocalImagePath(String path){
		
		this.localImagePath = path;
	}
	
	public void setRect(RectF rectF){
		
		this.rectF = rectF;
	}
	
	public RectF getRect(){
	
		return this.rectF;
	}
	
	public String getLocalImagePath(){
		
		return this.localImagePath;
	}
		
	public String getText(){
		
		return this.text;
	}
	public long getServerId() {
		return serverId;
	}
	
	public String getServerImagePath() {
		return serverImagePath;
	}

	
	public void setServerImagePath(String serverImagePath) {
		this.serverImagePath = serverImagePath;
	}
	
}
