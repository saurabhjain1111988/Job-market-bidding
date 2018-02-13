package com.market.jobmarket.project.dao;

import java.util.Date;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.order.PurchaseOrder;

public class PurchaseOrderDao extends BaseDao {

	private PreparedStatement upsert;
	private PreparedStatement selectByKey;
	
	private String table = DatabaseTableConstants.TABLE_PURCHASE_ORDER;
	
	
	public ResultSet selectByKeyUserId(Integer userId) throws Exception {
		if(null == this.selectByKey) {
			this.selectByKey = BaseDao.session.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_ID, userId);
		return BaseDao.session.execute(bs);
	}
	   
	public ResultSet selectByKey(Integer purchaseOrderId) throws Exception {
		if(null == this.selectByKey) {
			this.selectByKey = BaseDao.session.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_PURCHASE_ORDER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_ID, purchaseOrderId);
		return BaseDao.session.execute(bs);
	}
	
	public ResultSet upsert(PurchaseOrder purchaseOrder) throws Exception {
		if(null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, 
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_ID,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_ID,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_PROJECTS,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_STATUS,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_TIME,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_AMT,
					DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_DISPLAYNAME));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_ID, purchaseOrder.getId());
		bs.setInt(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_ID, purchaseOrder.getUserId());
		bs.setSet(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_PROJECTS, purchaseOrder.getProjects());
		if(null != purchaseOrder.getStatus()) {
			bs.setString(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_STATUS, purchaseOrder.getStatus().toString());
		}
		bs.set(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_TIME, purchaseOrder.getPurchaseTime(), Date.class);
		bs.setDouble(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_AMT, purchaseOrder.getPurchaseAmount());
		bs.setString(DatabaseTableConstants.COLUMN_PURCHASE_ORDER_USER_DISPLAYNAME, purchaseOrder.getUserDisplayName());
		return BaseDao.session.execute(bs);
	}
}
