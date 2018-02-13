package com.market.jobmarket.project.domain.user;

import java.util.Set;

import com.market.jobmarket.project.domain.user.BaseUser;

public class Buyer extends BaseUser {

	private Integer userId;

	private Set<Integer> biddingOrderIds;

	private Set<Integer> purchaseOrderIds;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Set<Integer> getBiddingOrderIds() {
		return biddingOrderIds;
	}

	public void setBiddingOrderIds(Set<Integer> biddingOrderIds) {
		this.biddingOrderIds = biddingOrderIds;
	}

	public Set<Integer> getPurchaseOrderIds() {
		return purchaseOrderIds;
	}

	public void setPurchaseOrderIds(Set<Integer> purchaseOrderIds) {
		this.purchaseOrderIds = purchaseOrderIds;
	}

}
