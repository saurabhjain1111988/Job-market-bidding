package com.market.jobmarket.project.domain.user;

import java.util.Set;


public class Seller {
	
	private Integer userId;

	private Set<Integer> listedProjectIds;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Set<Integer> getListedProjectIds() {
		return listedProjectIds;
	}

	public void setListedProjectIds(Set<Integer> listedProjectIds) {
		this.listedProjectIds = listedProjectIds;
	}	
		
}
