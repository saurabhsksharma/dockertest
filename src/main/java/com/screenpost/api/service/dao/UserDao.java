package com.screenpost.api.service.dao;

import java.util.List;

import com.screenpost.api.dto.UserProfileDto;
import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.util.ApiException;

public interface UserDao {

	/**
	 * @return
	 * @throws ApiException
	 */
	public List<UserProfileDto> findAll() throws ApiException;
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public UserProfileDto findById(int userId, String role) throws ApiException;

	public OauthUserDet findByUsername(String username) throws ApiException;
}
