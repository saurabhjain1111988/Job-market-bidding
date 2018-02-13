package com.market.jobmarket.project.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.market.jobmarket.project.dao.BiddingDao;
import com.market.jobmarket.project.dao.DatabaseTableConstants;
import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.domain.order.BidStatus;
import com.market.jobmarket.project.domain.project.BaseProjectInfo;
import com.market.jobmarket.project.domain.project.Project;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.BiddingService;
import com.market.jobmarket.project.service.ProjectService;

@Component
public class BiddingServiceImpl implements BiddingService {

	private static final Logger logger = LoggerFactory.getLogger(BiddingServiceImpl.class);

	@Autowired
	BiddingDao biddingDao;

	@Autowired
	ProjectService projectService;

	@Override
	public void placeBid(BidOrder bidOrder) throws JobMarketException {
		if (null == bidOrder.getProject() || null == bidOrder.getProject().getId()
				|| bidOrder.getProject().getId() == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "ProjectId can't be empty ");
		}
		Project project = projectService.getProject(bidOrder.getProject().getId());
		validateProjectToPlaceBid(project, bidOrder);

		ResultSet resultSet = null;
		try {
			resultSet = biddingDao.upsert(bidOrder);
			if (null != resultSet && null != resultSet.one()) {
				Row row = resultSet.one();
				bidOrder.setId(row.getInt(DatabaseTableConstants.COLUMN_BID_ORDER_ID));
				project.getBidOrderIds().add(bidOrder.getId());
				project.setCurrentBidOrder(bidOrder);
				projectService.updateProject(project);
			}
		} catch (Exception e) {
			logger.error("Exception occured while placing the bid" + e);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, e);
		}
	}

	@Override
	public void cancelBid(Integer bidOrderId) throws JobMarketException {
		BidOrder bidOrder = getBid(bidOrderId);
		bidOrder.setStatus(BidStatus.CANCELLED);
		try {
			biddingDao.upsert(bidOrder);
			removeBidFromProject(bidOrder);
		} catch (Exception e) {
			logger.error("Exception occured while Cancelling the bid" + e);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, e);
		}
	}

	@Override
	public void updateBidStatus(Integer bidOrderId, BidStatus status) throws JobMarketException {
		BidOrder bidOrder = getBid(bidOrderId);
		bidOrder.setStatus(status);
		try {
			biddingDao.upsert(bidOrder);
		} catch (Exception e) {
			logger.error("Exception occured while Updating the bid Status" + e);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, e);
		}
	}

	@Override
	public BidOrder getBid(Integer bidOrderId) throws JobMarketException {
		ResultSet resultSet = null;
		try {
			resultSet = biddingDao.selectByKey(bidOrderId);
		} catch (Exception e) {
			logger.error("Exception occured while getting the bid" + e);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, e);
		}
		if (null == resultSet) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, " Bidding Record not found ");
		}
		return getBidFromResultSetRow(resultSet.one());
	}

	@Override
	public List<BidOrder> getAllBids(Integer userId) {
		ResultSet resultSet = null;
		try {
			resultSet = biddingDao.selectByKeyUserId(userId);
		} catch (Exception e) {
			logger.error("Exception occured while getting the bid" + e);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, e);
		}

		return getBidOrdersFromResultSet(resultSet);
	}

	private void validateProjectToPlaceBid(Project project, BidOrder bitOrder) {

		if (null == project) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, "Project not found");
		}
		if (!projectService.isProjectOpenToBid(project)) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "Project is not open for bidding");
		}
		if (bitOrder.getBidAmount() < project.getCurrentBidOrder().getBidAmount()) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "Can't Bid below current Bid Amount");
		}
	}

	private void removeBidFromProject(BidOrder bidOrder) {
		Project project = projectService.getProject(bidOrder.getProject().getId());
		project.getBidOrderIds().remove(bidOrder.getId());

		// If this is the current Bid Order, update with the second highest bid
		if (project.getCurrentBidOrder().getId().equals(bidOrder.getId())) {
			BidOrder newMaxBidOrder = null;
			double maxBidOrderAmt = 0;
			for (Integer existingBidOrderId : project.getBidOrderIds()) {
				BidOrder existingBidOrder = getBid(existingBidOrderId);
				if (maxBidOrderAmt < existingBidOrder.getBidAmount()) {
					maxBidOrderAmt = existingBidOrder.getBidAmount();
					newMaxBidOrder = existingBidOrder;
				}
			}
			project.setCurrentBidOrder(newMaxBidOrder);
		}
	}

	private List<BidOrder> getBidOrdersFromResultSet(ResultSet resultSet) {
		List<BidOrder> bidOrders = new ArrayList<BidOrder>();
		if (null == resultSet || CollectionUtils.isEmpty(resultSet.all())) {
			return bidOrders;
		}

		for (Row row : resultSet.all()) {
			bidOrders.add(getBidFromResultSetRow(row));
		}
		return bidOrders;

	}

	private BidOrder getBidFromResultSetRow(Row row) {
		if (null == row) {
			return null;
		}
		Integer id = row.getInt(DatabaseTableConstants.COLUMN_BID_ORDER_ID);
		BaseProjectInfo baseProjectInfo = row.get(DatabaseTableConstants.COLUMN_BID_ORDER_PROJECT,
				BaseProjectInfo.class);
		double bidAmt = row.getDouble(DatabaseTableConstants.COLUMN_BID_ORDER_AMT);
		Date bidTime = row.getTimestamp(DatabaseTableConstants.COLUMN_BID_ORDER_TIME);
		BidStatus status = row.get(DatabaseTableConstants.COLUMN_BID_ORDER_STATUS, BidStatus.class);
		Integer userId = row.getInt(DatabaseTableConstants.COLUMN_BID_ORDER_USER_ID);
		String userDisplayName = row.getString(DatabaseTableConstants.COLUMN_BID_ORDER_USER_DISPLAYNAME);
		BidOrder bidOrder = new BidOrder(baseProjectInfo, bidAmt, bidTime, status, userId, userDisplayName);
		bidOrder.setId(id);
		return bidOrder;
	}
}
