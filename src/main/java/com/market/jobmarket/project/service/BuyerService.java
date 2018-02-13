package com.market.jobmarket.project.service;

import com.market.jobmarket.project.domain.user.Buyer;
import com.market.jobmarket.project.exception.JobMarketException;

public interface BuyerService {
	
	public void updateBuyer(Buyer user) throws JobMarketException;
	
	public Buyer getBuyer(Integer userId) throws JobMarketException;
	
}
