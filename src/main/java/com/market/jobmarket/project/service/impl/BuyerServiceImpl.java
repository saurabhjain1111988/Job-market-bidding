package com.market.jobmarket.project.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.market.jobmarket.project.dao.BuyerDao;
import com.market.jobmarket.project.dao.DatabaseTableConstants;
import com.market.jobmarket.project.domain.user.Buyer;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.BuyerService;

@Component
public class BuyerServiceImpl implements BuyerService {

	private static final Logger logger = LoggerFactory.getLogger(BuyerServiceImpl.class);

	@Autowired
	BuyerDao buyerDao;

	@Override
	public void updateBuyer(Buyer buyer) throws JobMarketException {
		if (null == buyer) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "Buyer can't be empty");
		}
		try {
			buyerDao.upsertBuyer(buyer);
		} catch (Exception ex) {
			logger.error("Exception occured while updating the buyer " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public Buyer getBuyer(Integer userId) throws JobMarketException {
		if (null == userId || userId == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "BuyerId can't be empty");
		}
		ResultSet resultSet = null;
		try {
			resultSet = buyerDao.selectByKey(userId);
		} catch (Exception ex) {
			logger.error("Exception occured while getting the buyer " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		return getBuyerFromResultSet(resultSet);
	}

	private Buyer getBuyerFromResultSet(ResultSet resultSet) {
		if (null == resultSet || null == resultSet.one()) {
			return null;
		}

		Row row = resultSet.one();
		Buyer buyer = new Buyer();
		buyer.setUserId(row.getInt(DatabaseTableConstants.COLUMN_USER_ID));

		Set<Integer> biddingOrderIds = row.getSet(DatabaseTableConstants.COLUMN_USER_BIDDING_ORDER_IDS, Integer.class);
		Set<Integer> biddingOrderIdsMutableSet = new HashSet<Integer>();
		biddingOrderIdsMutableSet.addAll(biddingOrderIds);
		buyer.setBiddingOrderIds(biddingOrderIdsMutableSet);

		Set<Integer> purchasedOrderIds = row.getSet(DatabaseTableConstants.COLUMN_USER_PURCHASED_ORDER_IDS,
				Integer.class);
		Set<Integer> purchasedOrderIdsMutableSet = new HashSet<Integer>();
		purchasedOrderIdsMutableSet.addAll(purchasedOrderIds);
		buyer.setBiddingOrderIds(purchasedOrderIdsMutableSet);

		return buyer;
	}

}
