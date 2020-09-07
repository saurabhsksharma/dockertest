package com.screenpost.api.service.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.service.dao.LoginDetDao;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.HibernateUtil;

@Component
public class LoginDetImpl implements LoginDetDao {

	@Override
	public void bcryptPasswords() {
		Session sess = HibernateUtil.getSessionFactory().openSession();

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		try {
			sess.beginTransaction();
			Query query=sess.createQuery("from OauthUserDet");
			for(int i=0;i<query.list().size();i++){
				OauthUserDet oauthUserDet = (OauthUserDet)query.list().get(i);
				if(oauthUserDet.getPassword() != null) {
					oauthUserDet.setPassword(passwordEncoder.encode(oauthUserDet.getPassword()));
					sess.update(oauthUserDet);
				}
			}
			sess.getTransaction().commit();
		} catch (HibernateException e) {
			sess.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			sess.clear();
			sess.close();
		}
	}

	@Override
	public OauthUserDet findByUsername(String username) {
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		OauthUserDet ldLoginDet = null;

		try {
			Criteria criteria = sess.createCriteria(OauthUserDet.class);
			criteria.add(Restrictions.eq("username", username));
			if (criteria.list() != null && criteria.list().size() > 0) {
				ldLoginDet = (OauthUserDet) criteria.list().get(0);
			}
		} catch (HibernateException e) {
			sess.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			sess.clear();
			sess.close();
		}
		
		return ldLoginDet;
	}

	@Override
	public OauthUserDet findByUserId(int userId) {
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		OauthUserDet ldLoginDet = null;
		
		try {
			ldLoginDet = (OauthUserDet) sess.get(OauthUserDet.class, userId);
		} catch (HibernateException e) {
			sess.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			sess.clear();
			sess.close();
		}
		
		return ldLoginDet;
	}

	@Override
	public OauthUserDet save(OauthUserDet ldLoginDet) throws ApiException {
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = sess.beginTransaction();
			
			sess.save(ldLoginDet);
			
			tx.commit();
			
			return ldLoginDet;
			
		} catch (HibernateException e) {
			throw new ApiException(e.getMessage());
		} finally {
			sess.clear();
			sess.close();
		}
	}

}
