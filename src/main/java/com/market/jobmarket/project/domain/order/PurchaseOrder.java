package com.market.jobmarket.project.domain.order;

import java.util.Date;
import java.util.Set;

import com.market.jobmarket.project.domain.project.BaseProjectInfo;

public class PurchaseOrder {

	private Integer id;

	private Set<BaseProjectInfo> projects;

	private Date purchaseTime;

	private double purchaseAmount;

	private PurchaseStatus status;

	private Integer userId;

	private String userDisplayName;

	public PurchaseOrder(Set<BaseProjectInfo> projects, Double purchaseAmount, PurchaseStatus status, Integer userId,
			String userDisplayName, Date purchaseTime) {
		this.projects = projects;
		this.purchaseAmount = purchaseAmount;
		this.purchaseTime = purchaseTime;
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

	public Set<BaseProjectInfo> getProjects() {
		return projects;
	}

	public void setProjects(Set<BaseProjectInfo> projects) {
		this.projects = projects;
	}

	public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public double getPurchaseAmount() {
		return purchaseAmount;
	}

	public void setPurchaseAmount(double purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
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

	public PurchaseStatus getStatus() {
		return status;
	}

	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}

}
