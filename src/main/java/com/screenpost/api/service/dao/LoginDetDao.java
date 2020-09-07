package com.screenpost.api.service.dao;

import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.util.ApiException;

public interface LoginDetDao {

	public void bcryptPasswords();

	public OauthUserDet findByUsername(String username);

	public OauthUserDet findByUserId(int userId);

	public OauthUserDet save(OauthUserDet ldLoginDet) throws ApiException;
}
