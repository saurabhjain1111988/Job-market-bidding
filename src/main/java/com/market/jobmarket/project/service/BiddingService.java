package com.market.jobmarket.project.service;

import java.util.List;

import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.domain.order.BidStatus;
import com.market.jobmarket.project.exception.JobMarketException;

public interface BiddingService {

	public void placeBid(BidOrder bidOrder) throws JobMarketException;
	
	public void cancelBid(Integer bidOrderId) throws JobMarketException;
	
	public void updateBidStatus(Integer bidOrderId, BidStatus status) throws JobMarketException;
	
	public BidOrder getBid(Integer bidOrderId) throws JobMarketException;
	
	public List<BidOrder> getAllBids(Integer userId) throws JobMarketException;
	
}
