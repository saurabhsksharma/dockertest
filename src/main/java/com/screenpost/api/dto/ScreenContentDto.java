package com.screenpost.api.dto;

import java.io.Serializable;
import java.sql.Time;

public class ScreenContentDto implements Serializable {

	private static final long serialVersionUID = -798722268495688408L;
	
	private int sno;
	private int screenId;
	private int contentId;
	private String title;
	private String url;
	private Time duration;
	private String type;
	
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public int getScreenId() {
		return screenId;
	}
	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDuration() {
		return duration.toString();
	}
	public void setDuration(Time duration) {
		this.duration = duration;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
