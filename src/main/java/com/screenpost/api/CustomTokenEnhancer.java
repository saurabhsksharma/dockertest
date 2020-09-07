package com.screenpost.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class CustomTokenEnhancer implements TokenEnhancer {
	@Autowired
	DataSource dataSource;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("username", authentication.getName());
        additionalInfo.put("roles", authentication.getAuthorities());
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			String query = "select user_id from oauth_user_det where username = '" + authentication.getName() + "'";
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			additionalInfo.put("userId", resultSet.getLong("user_id"));
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}        

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

}