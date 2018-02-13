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

	/**
	 * Places the bid using following steps 1) Get the project by using ProjectId
	 * from BidOrder 2) validate the project exists and open for Bidding, Also the
	 * Bid is greater than current Bidding 3) Insert the bid order 4) Update the
	 * Project with Current bid and insert the Bid Order Ids inside Project.
	 */
	@Override
	public void placeBid(BidOrder bidOrder) throws JobMarketException {
		// Step 1
		Project project = projectService.getProject(bidOrder.getProject().getId());
		// Step 2
		validateProjectToPlaceBid(project, bidOrder);

		ResultSet resultSet = null;
		try {
			// Step 3
			resultSet = biddingDao.upsert(bidOrder);
			if (null != resultSet && null != resultSet.one()) {
				Row row = resultSet.one();
				bidOrder.setId(row.getInt(DatabaseTableConstants.COLUMN_BID_ORDER_ID));
				// Step 4
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
		if (null == resultSet || null == resultSet.one()) {
			return null;
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
		if (null == resultSet || CollectionUtils.isEmpty(resultSet.all())) {
			return new ArrayList<BidOrder>();
		}

		return getBidOrdersFromResultSetRows(resultSet.all());
	}

	private void validateProjectToPlaceBid(Project project, BidOrder bitOrder) {
		if (null == project) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, "Project associated to Bid not found");
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
			// Already removed Bid from Project.getBidOrderIds()
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

	private List<BidOrder> getBidOrdersFromResultSetRows(List<Row> rows) {
		List<BidOrder> bidOrders = new ArrayList<BidOrder>();
		for (Row row : rows) {
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
