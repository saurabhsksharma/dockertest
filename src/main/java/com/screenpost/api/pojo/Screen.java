package com.screenpost.api.pojo;
// Generated 1 Jul, 2020 2:00:06 PM by Hibernate Tools 4.3.1.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Screen generated by hbm2java
 */
@Entity
@Table(name = "screen")
public class Screen implements java.io.Serializable {

	private Integer screenId;
	private Integer pinCode;
	private String screenName;
	private String deviceInfo;
	private Date connectedSince;
	private String timeZone;
	private Set<OauthUserDet> oauthUserDets = new HashSet(0);
//	private Set<ScreenContent> screenContentsForScreenId = new HashSet(0);

	public Screen() {
	}

	public Screen(Date connectedSince) {
		this.connectedSince = connectedSince;
	}

	public Screen(Integer pinCode, String screenName, String deviceInfo, Date connectedSince, String timeZone,
			Set<OauthUserDet> oauthUserDets/* , Set<ScreenContent> screenContentsForScreenId */) {
		this.pinCode = pinCode;
		this.screenName = screenName;
		this.deviceInfo = deviceInfo;
		this.connectedSince = connectedSince;
		this.timeZone = timeZone;
		this.oauthUserDets = oauthUserDets;
//		this.screenContentsForScreenId = screenContentsForScreenId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "screen_id", unique = true, nullable = false)
	public Integer getScreenId() {
		return this.screenId;
	}

	public void setScreenId(Integer screenId) {
		this.screenId = screenId;
	}

	@Column(name = "pin_code")
	public Integer getPinCode() {
		return this.pinCode;
	}

	public void setPinCode(Integer pinCode) {
		this.pinCode = pinCode;
	}

	@Column(name = "screen_name")
	public String getScreenName() {
		return this.screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	@Column(name = "device_info", length = 1073741824)
	public String getDeviceInfo() {
		return this.deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

//	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "connected_since", nullable = false, length = 19)
	public Date getConnectedSince() {
		return this.connectedSince;
	}

	public void setConnectedSince(Date connectedSince) {
		this.connectedSince = connectedSince;
	}

	@Column(name = "time_zone")
	public String getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "screen_user", joinColumns = {
			@JoinColumn(name = "screen_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", nullable = false, updatable = false) })
	public Set<OauthUserDet> getOauthUserDets() {
		return this.oauthUserDets;
	}

	public void setOauthUserDets(Set<OauthUserDet> oauthUserDets) {
		this.oauthUserDets = oauthUserDets;
	}
	
//	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "screenByScreenId")
//	public Set<ScreenContent> getScreenContentsForScreenId() {
//		return this.screenContentsForScreenId;
//	}
//
//	public void setScreenContentsForScreenId(Set<ScreenContent> screenContentsForScreenId) {
//		this.screenContentsForScreenId = screenContentsForScreenId;
//	}

}