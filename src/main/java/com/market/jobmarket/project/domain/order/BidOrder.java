package com.market.jobmarket.project.domain.order;

import java.util.Date;

import com.market.jobmarket.project.domain.project.BaseProjectInfo;

public class BidOrder {

	private Integer id;

	private BaseProjectInfo project;

	private BidStatus status;

	private final Date bidTime;

	private final double bidAmount;

	private Integer userId;

	private String userDisplayName;

	public BidOrder(BaseProjectInfo project, double bidAmount, Date bidTime, BidStatus status, Integer userId,
			String userDisplayName) {
		this.project = project;
		this.bidAmount = bidAmount;
		this.bidTime = bidTime;
		this.status = status;
		this.userId = userId;
		this.userDisplayName = userDisplayName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BaseProjectInfo getProject() {
		return project;
	}

	public void setProject(BaseProjectInfo project) {
		this.project = project;
	}

	public Date getBidTime() {
		return bidTime;
	}

	public double getBidAmount() {
		return bidAmount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public BidStatus getStatus() {
		return status;
	}

	public void setStatus(BidStatus status) {
		this.status = status;
	}

	public boolean isInvalid() {
		return (this.getBidAmount() <=0 || null == this.getProject() || null == this.getProject().getId() || this.getProject().getId() == 0
				|| null == this.getUserId() || this.getUserId() == 0);
	}

}
