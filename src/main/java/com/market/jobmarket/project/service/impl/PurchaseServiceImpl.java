package com.market.jobmarket.project.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.market.jobmarket.project.dao.DatabaseTableConstants;
import com.market.jobmarket.project.dao.PurchaseOrderDao;
import com.market.jobmarket.project.domain.order.PurchaseOrder;
import com.market.jobmarket.project.domain.order.PurchaseStatus;
import com.market.jobmarket.project.domain.project.BaseProjectInfo;
import com.market.jobmarket.project.domain.project.ProjectStatus;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.PurchaseService;

@Component
public class PurchaseServiceImpl implements PurchaseService {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseServiceImpl.class);

	@Autowired
	PurchaseOrderDao purchaseOrderDao;

	@Override
	public void placePurchaseOrder(PurchaseOrder purchaseOrder) throws JobMarketException {
		updatePurchaseOrder(purchaseOrder);
	}

	@Override
	public List<PurchaseOrder> getPurchaseOrder(Integer userId) throws JobMarketException {
		ResultSet resultSet = null;
		try {
			resultSet = purchaseOrderDao.selectByKeyUserId(userId);
		} catch (Exception ex) {
			logger.error("Exception occured while getting the Purchase Order " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		if (null == resultSet || CollectionUtils.isEmpty(resultSet.all())) {
			return new ArrayList<PurchaseOrder>();
		}
		return getPurchaseOrdersFromResultSetRows(resultSet.all());
	}

	@Override
	public void cancelOrder(Integer purchaseId) throws JobMarketException {
		ResultSet resultSet = null;
		try {
			resultSet = purchaseOrderDao.selectByKeyUserId(purchaseId);
		} catch (Exception ex) {
			logger.error("Exception occured while cancelling the Purchase Order " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		if (null == resultSet || null == resultSet.one()) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, "Purchase Order not found");
		}
		PurchaseOrder purchaseOrder = getPurchaseOrderFromResultSetRow(resultSet.one());
		purchaseOrder.setStatus(PurchaseStatus.CANCELLED);
		updatePurchaseOrder(purchaseOrder);
	}

	private void updatePurchaseOrder(PurchaseOrder purchaseOrder) {
		try {
			purchaseOrderDao.upsert(purchaseOrder);
		} catch (Exception ex) {
			logger.error("Exception occured while cancelling the Purchase Order " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	private List<PurchaseOrder> getPurchaseOrdersFromResultSetRows(List<Row> rows) {
		List<PurchaseOrder> purchaseOrders = new ArrayList<PurchaseOrder>();
		for (Row row : rows) {
			purchaseOrders.add(getPurchaseOrderFromResultSetRow(row));
		}
		return purchaseOrders;
	}

	private PurchaseOrder getPurchaseOrderFromResultSetRow(Row row) {
		if(null == row) {
			return null;
		}
		Integer id  = row.getInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_ID);
		Integer userId = row.getInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_ID);
		PurchaseStatus status = row.get(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_STATUS, PurchaseStatus.class);
		Set<BaseProjectInfo> projects = row.getSet(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_PROJECTS, BaseProjectInfo.class);
		Date purchaseTime= row.get(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_TIME, Date.class);
		Double purchaseAmount = row.getDouble(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_AMT);
		String userDisplayName = row.getString(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_DISPLAYNAME);
		
		
		PurchaseOrder purchaseOrder = new PurchaseOrder(projects, purchaseAmount, status, userId, userDisplayName, purchaseTime);
		purchaseOrder.setId(id);
		return purchaseOrder;
	}

}
