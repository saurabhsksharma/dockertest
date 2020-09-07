package com.screenpost.api.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.screenpost.api.dto.UserProfileDto;
import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.pojo.UserProfile;
import com.screenpost.api.service.dao.UserDao;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.HibernateUtil;

@Component
public class UserImpl implements UserDao {
	
	private static final Logger LOG = Logger.getLogger(UserImpl.class);

	@Override
	public List<UserProfileDto> findAll() throws ApiException {
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		List<UserProfileDto> userProfileDto = null;
		
		try {
			Criteria criteria = sess.createCriteria(UserProfile.class, "userProfile");
			criteria.createAlias("userProfile.oauthUserDet", "oauthUser", JoinType.INNER_JOIN);
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("userProfile.firstName"), "firstName");
			projections.add(Projections.property("userProfile.lastName"), "lastName");
			projections.add(Projections.property("userProfile.companyName"), "companyName");
			projections.add(Projections.property("oauthUser.username"), "username");
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(UserProfileDto.class));	
			
			userProfileDto = criteria.list();
			
			
		} catch (HibernateException e) {
			LOG.error(e);
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return userProfileDto;
	}

	@Override
	public UserProfileDto findById(int userId, String role) throws ApiException {

		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		UserProfileDto userProfileDto = null;
		
		try {
			Criteria criteria = sess.createCriteria(UserProfile.class, "userProfile");
			criteria.createAlias("userProfile.oauthUserDet", "oauthUser", JoinType.INNER_JOIN);
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("userProfile.firstName"), "firstName");
			projections.add(Projections.property("userProfile.lastName"), "lastName");
			projections.add(Projections.property("userProfile.companyName"), "companyName");
			projections.add(Projections.property("oauthUser.username"), "username");
			
			criteria.add(Restrictions.eq("oauthUser.userId", userId));
			criteria.add(Restrictions.eq("oauthUser.role", role));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(UserProfileDto.class));	
			
			if(criteria.list() != null && criteria.list().size() > 0) {
				userProfileDto = (UserProfileDto) criteria.list().get(0);
			}
			
			
		} catch (HibernateException e) {
			LOG.error(e);
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return userProfileDto;
	}

	@Override
	public OauthUserDet findByUsername(String username) throws ApiException {

		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		OauthUserDet oauthUserDet = null;
		
		try {
			Criteria criteria = sess.createCriteria(OauthUserDet.class, "oauthUser");
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("oauthUser.userId"), "userId");
			projections.add(Projections.property("oauthUser.username"), "username");
			projections.add(Projections.property("oauthUser.role"), "role");
			projections.add(Projections.property("oauthUser.active"), "active");
			projections.add(Projections.property("oauthUser.provider"), "provider");
			projections.add(Projections.property("oauthUser.createdAt"), "createdAt");
			projections.add(Projections.property("oauthUser.lastModifiedAt"), "lastModifiedAt");
			
			criteria.add(Restrictions.eq("oauthUser.username", username));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(OauthUserDet.class));	
			
			if(criteria.list() != null && criteria.list().size() > 0) {
				oauthUserDet = (OauthUserDet) criteria.list().get(0);
			}
			
		} catch (HibernateException e) {
			LOG.error(e);
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return oauthUserDet;
	}

}
