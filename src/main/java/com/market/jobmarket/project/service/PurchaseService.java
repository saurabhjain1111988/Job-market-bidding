package com.market.jobmarket.project.service;

import java.util.List;

import com.market.jobmarket.project.domain.order.PurchaseOrder;
import com.market.jobmarket.project.exception.JobMarketException;

public interface PurchaseService {

	public void placePurchaseOrder(PurchaseOrder purchaseOrder) throws JobMarketException;
	
	public List<PurchaseOrder> getPurchaseOrder(Integer userId) throws JobMarketException;
	
	public void cancelOrder(Integer purchaseId) throws JobMarketException;
}
