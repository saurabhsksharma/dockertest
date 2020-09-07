package com.screenpost.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.service.dao.LoginDetDao;
import com.screenpost.api.util.ApiException;

@Service
public class LoginService {
	
	@Autowired
	LoginDetDao loginDetDao;

	public OauthUserDet loadUserByUsername(String username) throws UsernameNotFoundException {
		OauthUserDet user = loginDetDao.findByUsername(username);
        return user;
	}
	
	public OauthUserDet getLoginDetById(int userId) throws ApiException {
		return loginDetDao.findByUserId(userId);
	}
	
	public OauthUserDet saveLogin(OauthUserDet ldLoginDet) throws ApiException {
		return loginDetDao.save(ldLoginDet);
	}
	
}
