package com.screenpost.api.service.dao;

import java.util.List;

import com.screenpost.api.dto.ScreenContentDto;
import com.screenpost.api.pojo.Screen;
import com.screenpost.api.pojo.ScreenContent;
import com.screenpost.api.util.ApiException;

public interface ScreenDao {

	/**
	 * @return
	 * @throws ApiException
	 */
	public List<Screen> findAllByUserId(int userId) throws ApiException;

	/**
	 * @param screenId
	 * @return
	 */
	public Screen findById(int screenId) throws ApiException;
	
	public Screen createByPinCode(int pinCode, int userId) throws ApiException;
	
	public ScreenContent addContentToScreen(int screenId, int contentId) throws ApiException;
	
	public List<ScreenContentDto> getScreenContent(int screenId) throws ApiException;
}
