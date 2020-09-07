package com.screenpost.api.service.dao;

import java.util.List;

import com.screenpost.api.pojo.ContainerContent;
import com.screenpost.api.pojo.ContainerContentId;
import com.screenpost.api.pojo.Content;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.ContentType;

public interface ContentDao {

	public List<Content> findAllContentByUser(int userId) throws ApiException;
	
	public List<Content> findAllContentByType(int userId, ContentType type) throws ApiException;
	
	public void addContentInScreen(String type, int contentId, int screenId) throws ApiException;
	
	public List<ContainerContent> addContentInContainer(Integer containerId, Integer[] contentIds) throws ApiException;

	public List<ContainerContent> addContentInContainer(String containerTitle, Integer[] contentIds, ContentType type, int userId) throws ApiException;
	
	public Content saveContent(int userId, ContentType type, String url, String title, long duration) throws ApiException;

	public List<Content> getContentInContainer(Integer containerId, Integer userId) throws ApiException;
	
}
