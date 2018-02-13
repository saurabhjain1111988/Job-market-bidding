package com.market.jobmarket.project.dao;

import java.util.Date;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.domain.project.BaseProjectInfo;

public class BiddingDao extends BaseDao {

	private PreparedStatement upsert;
	private PreparedStatement selectByKey;
	private PreparedStatement selectAll;
	
	private String table = DatabaseTableConstants.TABLE_BID_ORDER;
	
	public ResultSet selectAll() throws Exception {
		if(null == this.selectAll) {
			this.selectAll = BaseDao.session.prepare(getSelectAll(table));
		}
		return BaseDao.session.execute(this.selectAll.bind());
	}
	
	public ResultSet selectByKeyUserId(Integer userId) throws Exception {
		if(null == this.selectByKey) {
			this.selectByKey = BaseDao.session.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_BID_ORDER_USER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_BID_ORDER_USER_ID, userId);
		return BaseDao.session.execute(bs);
	}
	   
	public ResultSet selectByKey(Integer bidOrderId) throws Exception {
		if(null == this.selectByKey) {
			this.selectByKey = BaseDao.session.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_BID_ORDER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_BID_ORDER_ID, bidOrderId);
		return BaseDao.session.execute(bs);
	}
	
	public ResultSet upsert(BidOrder bidOrder) throws Exception {
		if(null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, 
				  	DatabaseTableConstants.COLUMN_BID_ORDER_ID,
				  	DatabaseTableConstants.COLUMN_BID_ORDER_USER_ID,
				  	DatabaseTableConstants.COLUMN_BID_ORDER_PROJECT,
				  	DatabaseTableConstants.COLUMN_BID_ORDER_STATUS,		     
				    DatabaseTableConstants.COLUMN_BID_ORDER_TIME,
				  	DatabaseTableConstants.COLUMN_BID_ORDER_AMT,
				  	DatabaseTableConstants.COLUMN_BID_ORDER_USER_DISPLAYNAME));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_BID_ORDER_ID, bidOrder.getId());
		bs.setInt(DatabaseTableConstants.COLUMN_BID_ORDER_USER_ID, bidOrder.getUserId());
		bs.set(DatabaseTableConstants.COLUMN_BID_ORDER_PROJECT, bidOrder.getProject(), BaseProjectInfo.class);
		if(null != bidOrder.getStatus()) {
			bs.setString(DatabaseTableConstants.COLUMN_BID_ORDER_STATUS, bidOrder.getStatus().toString());
		}
		bs.set(DatabaseTableConstants.COLUMN_BID_ORDER_TIME, bidOrder.getBidTime(), Date.class);
		bs.setDouble(DatabaseTableConstants.COLUMN_BID_ORDER_AMT, bidOrder.getBidAmount());
		bs.setString(DatabaseTableConstants.COLUMN_BID_ORDER_USER_DISPLAYNAME, bidOrder.getUserDisplayName());
		return BaseDao.session.execute(bs);
	}
}
