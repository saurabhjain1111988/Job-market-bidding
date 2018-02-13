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
import com.market.jobmarket.project.dao.SellerDao;
import com.market.jobmarket.project.dao.UserDao;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.domain.user.Buyer;
import com.market.jobmarket.project.domain.user.Seller;
import com.market.jobmarket.project.domain.user.UserStatus;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.BuyerService;
import com.market.jobmarket.project.service.SellerService;
import com.market.jobmarket.project.service.UserService;

@Component
public class SellerServiceImpl implements SellerService {

	private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);

	@Autowired
	SellerDao sellerDao;

	@Override
	public void updateSeller(Seller seller) throws JobMarketException {
		if (null == seller) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "Seller can't be empty");
		}
		try {
			sellerDao.upsertSeller(seller);
		} catch (Exception ex) {
			logger.error("Exception occured while updating the Seller " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public Seller getSeller(Integer userId) throws JobMarketException {
		if (null == userId || userId == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "SellerId can't be empty");
		}
		ResultSet resultSet = null;
		try {
			resultSet = sellerDao.selectByKey(userId);
		} catch (Exception ex) {
			logger.error("Exception occured while getting the Seller " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		return getSellerFromResultSet(resultSet);
	}

	private Seller getSellerFromResultSet(ResultSet resultSet) {
		if (null == resultSet || null == resultSet.one()) {
			return null;
		}

		Row row = resultSet.one();
		Seller seller = new Seller();
		seller.setUserId(row.getInt(DatabaseTableConstants.COLUMN_USER_ID));

		Set<Integer> listedProjectIds = row.getSet(DatabaseTableConstants.COLUMN_USER_LISTED_PROJECT_IDS,
				Integer.class);
		Set<Integer> listedProjectIdsMutableSet = new HashSet<Integer>();
		listedProjectIdsMutableSet.addAll(listedProjectIds);
		seller.setListedProjectIds(listedProjectIdsMutableSet);

		return seller;
	}

}
