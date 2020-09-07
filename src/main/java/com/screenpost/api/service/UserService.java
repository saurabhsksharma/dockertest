package com.screenpost.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.screenpost.api.dto.UserProfileDto;
import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.service.dao.UserDao;
import com.screenpost.api.util.ApiException;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;

	public List<UserProfileDto> getAllUser() throws ApiException {
		return userDao.findAll();
	}
	public UserProfileDto getUserById(int userId, String role) throws ApiException {
		return userDao.findById(userId, role);
	}
	
	public OauthUserDet getUserByUsername(String username) throws ApiException {
		return userDao.findByUsername(username);
	}
}
