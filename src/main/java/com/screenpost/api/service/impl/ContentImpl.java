package com.screenpost.api.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.screenpost.api.pojo.ContainerContent;
import com.screenpost.api.pojo.ContainerContentId;
import com.screenpost.api.pojo.Content;
import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.service.dao.ContentDao;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.ContentType;
import com.screenpost.api.util.HibernateUtil;

@Component
public class ContentImpl implements ContentDao {
	
	private static final Logger LOG = Logger.getLogger(ContentImpl.class);

	@Override
	public List<Content> findAllContentByUser(int userId) throws ApiException {
		
		List<Content> contentList = null;
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		try {
			Criteria criteria = sess.createCriteria(Content.class, "content");
			criteria.createAlias("content.oauthUserDet", "user", JoinType.INNER_JOIN);
			
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("content.contentId"),"contentId");
			projections.add(Projections.property("content.title"),"title");
			projections.add(Projections.property("content.folder"),"folder");
			projections.add(Projections.property("content.type"),"type");
			projections.add(Projections.property("content.url"),"url");
			projections.add(Projections.property("content.duration"),"duration");
			projections.add(Projections.property("content.info"),"info");
			projections.add(Projections.property("content.createdAt"),"createdAt");
			
			criteria.add(Restrictions.eq("user.userId", userId));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(Content.class));	
			
			contentList = criteria.list();
			
		} catch (HibernateException e) {
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		return contentList;
	}

	@Override
	public List<Content> findAllContentByType(int userId, ContentType type) throws ApiException {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		List<Content> contentList = null;
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		try {
			Criteria criteria = sess.createCriteria(Content.class, "content");
			criteria.createAlias("content.oauthUserDet", "user", JoinType.INNER_JOIN);
			
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("content.contentId"),"contentId");
			projections.add(Projections.property("content.title"),"title");
			projections.add(Projections.property("content.folder"),"folder");
			projections.add(Projections.property("content.type"),"type");
			projections.add(Projections.property("content.url"),"url");
			projections.add(Projections.property("content.duration"),"duration");
			projections.add(Projections.property("content.info"),"info");
			projections.add(Projections.property("content.createdAt"),"createdAt");
			
			criteria.add(Restrictions.eq("user.userId", userId));
			criteria.add(Restrictions.eq("content.type", type.getType()));
			criteria.addOrder(Order.desc("content.createdAt"));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(Content.class));	
			
			contentList = criteria.list();
			
		} catch (HibernateException e) {
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		return contentList;
	}
	
	@Override
	public List<ContainerContent> addContentInContainer(Integer containerId, Integer[] contentIds) throws ApiException {

		boolean isNewSession = false;
		Session sess = null;
		if(HibernateUtil.getSessionFactory().isClosed()) {
			sess = HibernateUtil.getSessionFactory().openSession();
			isNewSession = true;
		} else {
			sess = HibernateUtil.getSessionFactory().getCurrentSession();
		}
		Transaction tx = sess.beginTransaction();
		
		List<ContainerContent> containerContentList = null;
		
		try {
			Criteria criteria = sess.createCriteria(ContainerContent.class);
			
			criteria.add(Restrictions.eq("id.containerId", containerId));
			criteria.addOrder(Order.desc("sno"));
			
			containerContentList = criteria.list();
			
			int sno = 0;
			if(containerContentList != null && containerContentList.size() > 0)
				sno = containerContentList.get(0).getSno();
			
			for(int contentId: contentIds) {
				ContainerContent containerContent = new ContainerContent();
				ContainerContentId containerContentId = new ContainerContentId();
				
				Content container = (Content) sess.get(Content.class, containerId);
				containerContent.setContentByContainerId(container);

				Content content = (Content) sess.get(Content.class, contentId);
				containerContent.setContentByContentId(content);
				
				Time currentDuration = container.getDuration();
				currentDuration.setTime(currentDuration.getTime()+content.getDuration().getTime());
				container.setDuration(currentDuration);
				sess.update(container);
				
				containerContentId.setContainerId(containerId);
				containerContentId.setContentId(contentId);
				containerContent.setId(containerContentId);
				
				containerContent.setSno(++sno);
				
				containerContentId = (ContainerContentId) sess.save(containerContent);
				
				containerContentList.add(containerContent);
			}
			
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
//			e.printStackTrace();
		} finally {
			if(isNewSession)
				sess.close();
		}
		
		return containerContentList;
		
	}

	@Override
	public List<ContainerContent> addContentInContainer(String title, Integer[] contentIds, ContentType type, int userId) throws ApiException {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = sess.beginTransaction();
		
		List<ContainerContent> containerContentList = null;
		Content content = null;
		
		try {
			content = new Content();
			OauthUserDet oauthUserDet = (OauthUserDet) sess.get(OauthUserDet.class, userId);
			content.setOauthUserDet(oauthUserDet);
			content.setTitle(title);
			content.setType(type.getType());
			content.setDuration(new Time(0));
			content.setCreatedAt(new Timestamp(Calendar.getInstance().getTime().getTime()));
			
			sess.save(content);	
			tx.commit();
			
			containerContentList = addContentInContainer(content.getContentId(), contentIds);
			
		} catch (Exception e) {
			tx.rollback();
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		} 
		
		return containerContentList;
	}

	@Override
	public void addContentInScreen(String type, int contentId, int screenId) throws ApiException {
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			sess.close();
		}
		
	}

	@Override
	public Content saveContent(int userId, ContentType type, String url, String title, long duration) throws ApiException {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		Session sess = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = sess.beginTransaction();
		
		Content content = null;
		
		try {
			content = new Content();
			OauthUserDet oauthUserDet = (OauthUserDet) sess.get(OauthUserDet.class, userId);
			content.setOauthUserDet(oauthUserDet);
			content.setTitle(title);
			content.setUrl(url);
			content.setFolder(null);
			content.setInfo(null);
			content.setType(type.getType());
			content.setDuration(new Time(duration));
			content.setCreatedAt(new Timestamp(Calendar.getInstance().getTime().getTime()));
			
			sess.save(content);
			tx.commit();
			
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
//			e.printStackTrace();
		} finally {
			sess.close();
		}
		
		return content;
	}

	@Override
	public List<Content> getContentInContainer(Integer containerId, Integer userId) throws ApiException {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		List<Content> contentList = null;
		
		try {
			Criteria criteria = sess.createCriteria(ContainerContent.class, "containerContent");
			criteria.createAlias("containerContent.contentByContentId", "content", JoinType.INNER_JOIN);
			criteria.createAlias("content.oauthUserDet", "user", JoinType.INNER_JOIN);
			
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("content.contentId"),"contentId");
			projections.add(Projections.property("content.title"),"title");
			projections.add(Projections.property("content.folder"),"folder");
			projections.add(Projections.property("content.type"),"type");
			projections.add(Projections.property("content.url"),"url");
			projections.add(Projections.property("content.duration"),"duration");
			projections.add(Projections.property("content.info"),"info");
			projections.add(Projections.property("content.createdAt"),"createdAt");
			
			criteria.add(Restrictions.eq("user.userId", userId));
			criteria.add(Restrictions.eq("containerContent.id.containerId", containerId));
			criteria.addOrder(Order.asc("containerContent.sno"));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(Content.class));	
			
			contentList = criteria.list();
		} catch (HibernateException e) {
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return contentList;
	}

}
