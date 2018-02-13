package com.market.jobmarket.project.service;

import com.market.jobmarket.project.domain.user.Seller;
import com.market.jobmarket.project.exception.JobMarketException;

public interface SellerService {
	
	public void updateSeller(Seller user) throws JobMarketException;
	
	public Seller getSeller(Integer userId) throws JobMarketException;
}
