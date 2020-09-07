package com.screenpost.api.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.screenpost.api.dto.ScreenContentDto;
import com.screenpost.api.pojo.ContainerContent;
import com.screenpost.api.pojo.OauthUserDet;
import com.screenpost.api.pojo.Screen;
import com.screenpost.api.pojo.ScreenContent;
import com.screenpost.api.pojo.ScreenContentId;
import com.screenpost.api.service.dao.ScreenDao;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.ContentType;
import com.screenpost.api.util.HibernateUtil;

@Component
public class ScreenImpl implements ScreenDao {
	
	private static final Logger LOG = Logger.getLogger(ScreenImpl.class);

	@Override
	public List<Screen> findAllByUserId(int userId) throws ApiException {
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		List<Screen> userProfileDto = null;
		
		try {
			Criteria criteria = sess.createCriteria(Screen.class, "screen");
			criteria.createAlias("screen.oauthUserDets", "userDet", JoinType.INNER_JOIN);
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("screen.screenId"), "screenId");
			projections.add(Projections.property("screen.pinCode"), "pinCode");
			projections.add(Projections.property("screen.screenName"), "screenName");
			projections.add(Projections.property("screen.deviceInfo"), "deviceInfo");
			projections.add(Projections.property("screen.connectedSince"), "connectedSince");
			projections.add(Projections.property("screen.timeZone"), "timeZone");
			
			criteria.add(Restrictions.eq("userDet.userId", userId));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(Screen.class));	
			
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
	public Screen findById(int screenId) throws ApiException {

		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		Screen screen = null;
		
		try {
			Criteria criteria = sess.createCriteria(Screen.class, "screen");
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("screen.screenId"), "screenId");
			projections.add(Projections.property("screen.pinCode"), "pinCode");
			projections.add(Projections.property("screen.screenName"), "screenName");
			projections.add(Projections.property("screen.deviceInfo"), "deviceInfo");
			projections.add(Projections.property("screen.connectedSince"), "connectedSince");
			projections.add(Projections.property("screen.timeZone"), "timeZone");
			
			criteria.add(Restrictions.eq("screen.screenId", screenId));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(Screen.class));	
			
			if(criteria.list() != null && criteria.list().size() > 0)
				screen = (Screen) criteria.list().get(0);
			
			
		} catch (HibernateException e) {
			LOG.error(e);
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return screen;
	}

	@Override
	public Screen createByPinCode(int pinCode, int userId) throws ApiException {

		Session sess = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = sess.beginTransaction();
		
		Screen screen = null;
		
		try {
			screen = new Screen();
			OauthUserDet oauthUserDet = (OauthUserDet) sess.get(OauthUserDet.class, userId);
			Set<OauthUserDet> oauthUserDets = new HashSet<OauthUserDet>();
			oauthUserDets.add(oauthUserDet);
			screen.setOauthUserDets(oauthUserDets);
			screen.setDeviceInfo(null);
			screen.setPinCode(pinCode);
			screen.setScreenName(String.valueOf(pinCode));
			screen.setTimeZone(TimeZone.getTimeZone("GMT").getDisplayName());
			
			sess.save(screen);
			tx.commit();
			
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
//			e.printStackTrace();
		} finally {
			sess.close();
		}
		return screen;
	}

	@Override
	public ScreenContent addContentToScreen(int screenId, int contentId) throws ApiException {

		Session sess = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = sess.beginTransaction();
		
		ScreenContent screenContent = null;
		
		try {
			Criteria criteria = sess.createCriteria(ScreenContent.class, "screenContent");
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.max("screenContent.sno"), "sno");

			criteria.add(Restrictions.eq("screenContent.id.screenId", screenId));
			criteria.addOrder(Order.desc("screenContent.sno"));
			
			criteria.setProjection(projections);
//			criteria.setResultTransformer(Transformers.aliasToBean(ScreenContent.class));	
			
			Integer maxSno = 0;
			if(criteria.list() != null && criteria.list().size() > 0)
				maxSno = (Integer) criteria.list().get(0);
			
			screenContent = new ScreenContent();
			ScreenContentId screenContentId = new ScreenContentId();
			screenContentId.setScreenId(screenId);
			screenContentId.setContentId(contentId);
			screenContent.setId(screenContentId);
			if(maxSno != null) {
				screenContent.setSno(maxSno+1);
			} else {
				screenContent.setSno(1);
			}
			
			
			sess.save(screenContent);
			tx.commit();
			
		} catch (HibernateException e) {
			tx.rollback();
			LOG.error(e.getMessage());
			throw new ApiException(e.getMessage());
//			e.printStackTrace();
		} finally {
			
		}
		return screenContent;
	}

	@Override
	public List<ScreenContentDto> getScreenContent(int screenId) throws ApiException {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Session sess = HibernateUtil.getSessionFactory().openSession();
		
		List<ScreenContentDto> screenContentList = new ArrayList<>();
		
		int sno = 0;
		
		try {
			Criteria criteria = sess.createCriteria(ScreenContent.class, "screenContent");
			criteria.createAlias("screenContent.contentByContentId", "content", JoinType.INNER_JOIN);
			ProjectionList projections = Projections.projectionList();
			projections.add(Projections.property("screenContent.sno"), "sno");
			projections.add(Projections.property("screenContent.id.screenId"), "screenId");
			projections.add(Projections.property("screenContent.id.contentId"), "contentId");
			projections.add(Projections.property("content.title"), "title");
			projections.add(Projections.property("content.type"), "type");
			projections.add(Projections.property("content.url"), "url");
			projections.add(Projections.property("content.duration"), "duration");
			
			
			criteria.add(Restrictions.eq("screenContent.id.screenId", screenId));
			
			criteria.setProjection(projections);
			criteria.setResultTransformer(Transformers.aliasToBean(ScreenContentDto.class));
			
			List<ScreenContentDto> screenContentDtoList = null;
			
			if(criteria.list() != null && criteria.list().size() > 0) {
				screenContentDtoList = (List<ScreenContentDto>) criteria.list();
				
				for(ScreenContentDto screenContentDto: screenContentDtoList) {

					if(ContentType.IMAGE.getType().equals(screenContentDto.getType()) || 
							ContentType.VIDEO.getType().equals(screenContentDto.getType()) || 
							ContentType.LINK.getType().equals(screenContentDto.getType())) {
						sno += 1;
						screenContentDto.setSno(sno);
						screenContentList.add(screenContentDto);
					} else if(ContentType.CHANNEL.getType().equals(screenContentDto.getType())) {
						Criteria criteria2 = sess.createCriteria(ContainerContent.class, "containerContent");
						criteria2.createAlias("containerContent.contentByContentId", "content", JoinType.INNER_JOIN);
						ProjectionList projections2 = Projections.projectionList();
						projections2.add(Projections.property("containerContent.sno"), "sno");
						projections2.add(Projections.property("containerContent.id.containerId"), "screenId");
						projections2.add(Projections.property("containerContent.id.contentId"), "contentId");
						projections2.add(Projections.property("content.title"), "title");
						projections2.add(Projections.property("content.type"), "type");
						projections2.add(Projections.property("content.url"), "url");
						projections2.add(Projections.property("content.duration"), "duration");
						
						criteria2.add(Restrictions.eq("containerContent.id.containerId", screenContentDto.getContentId()));
						
						criteria2.setProjection(projections2);
						criteria2.setResultTransformer(Transformers.aliasToBean(ScreenContentDto.class));
						
						if(criteria2.list() != null && criteria2.list().size() > 0) {
							List<ScreenContentDto> channelContentList = (List<ScreenContentDto>) criteria2.list();
							
							for(ScreenContentDto channelContentDto: channelContentList) {
								if(ContentType.IMAGE.getType().equals(channelContentDto.getType()) || 
										ContentType.VIDEO.getType().equals(channelContentDto.getType()) || 
										ContentType.LINK.getType().equals(channelContentDto.getType())) {
									sno += 1;
									channelContentDto.setSno(sno);
									screenContentList.add(channelContentDto);
								} else if(ContentType.PLAYLIST.getType().equals(channelContentDto.getType())) {
									Criteria criteria3 = sess.createCriteria(ContainerContent.class, "containerContent");
									criteria3.createAlias("containerContent.contentByContentId", "content", JoinType.INNER_JOIN);
									ProjectionList projections3 = Projections.projectionList();
									projections3.add(Projections.property("containerContent.sno"), "sno");
									projections3.add(Projections.property("containerContent.id.containerId"), "screenId");
									projections3.add(Projections.property("containerContent.id.contentId"), "contentId");
									projections3.add(Projections.property("content.title"), "title");
									projections3.add(Projections.property("content.type"), "type");
									projections3.add(Projections.property("content.url"), "url");
									projections3.add(Projections.property("content.duration"), "duration");
									
									criteria3.add(Restrictions.eq("containerContent.id.containerId", channelContentDto.getContentId()));
									
									criteria3.setProjection(projections3);
									criteria3.setResultTransformer(Transformers.aliasToBean(ScreenContentDto.class));
									
									if(criteria3.list() != null && criteria3.list().size() > 0) {
										List<ScreenContentDto> channelPlaylistContentList = (List<ScreenContentDto>) criteria3.list();
										
										for(ScreenContentDto channelPlaylistContent: channelPlaylistContentList) {
											sno += 1;
											channelPlaylistContent.setSno(sno);
										}
										
										screenContentList.addAll(channelPlaylistContentList);
									}
								}
							}							
							
							
						}
					} else if(ContentType.PLAYLIST.getType().equals(screenContentDto.getType())) {
						Criteria criteria4 = sess.createCriteria(ContainerContent.class, "containerContent");
						criteria4.createAlias("containerContent.contentByContentId", "content", JoinType.INNER_JOIN);
						ProjectionList projections4 = Projections.projectionList();
						projections4.add(Projections.property("containerContent.sno"), "sno");
						projections4.add(Projections.property("containerContent.id.containerId"), "screenId");
						projections4.add(Projections.property("containerContent.id.contentId"), "contentId");
						projections4.add(Projections.property("content.title"), "title");
						projections4.add(Projections.property("content.type"), "type");
						projections4.add(Projections.property("content.url"), "url");
						projections4.add(Projections.property("content.duration"), "duration");
						
						criteria4.add(Restrictions.eq("containerContent.id.containerId", screenContentDto.getContentId()));
						
						criteria4.setProjection(projections4);
						criteria4.setResultTransformer(Transformers.aliasToBean(ScreenContentDto.class));
						
						if(criteria4.list() != null && criteria4.list().size() > 0) {
							List<ScreenContentDto> playlistContentList = (List<ScreenContentDto>) criteria4.list();
							
							for(ScreenContentDto playlistContent: playlistContentList) {
								sno += 1;
								playlistContent.setSno(sno);
							}
							
							screenContentList.addAll(playlistContentList);
						}
					}
				}
			}
			
			
		} catch (HibernateException e) {
			LOG.error(e);
			throw new ApiException(e.getMessage());
		} finally {
			sess.close();
		}
		
		return screenContentList;
	}

}
