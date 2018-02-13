package com.market.jobmarket.project.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.user.Buyer;

public class BuyerDao extends BaseDao {
	private PreparedStatement upsert;
	private PreparedStatement selectByKey;

	private String table = DatabaseTableConstants.TABLE_BUYER;

	public ResultSet selectByKey(Integer userId) throws Exception {
		if (null == this.selectByKey) {
			this.selectByKey = BaseDao.session
					.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_USER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_USER_ID, userId);
		return BaseDao.session.execute(bs);
	}

	public ResultSet upsertBuyer(Buyer user) throws Exception {
		if (null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, DatabaseTableConstants.COLUMN_USER_ID,
					DatabaseTableConstants.COLUMN_USER_PURCHASED_ORDER_IDS));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_USER_ID, user.getUserId());
		if (null != user.getBiddingOrderIds()) {
			bs.setSet(DatabaseTableConstants.COLUMN_USER_BIDDING_ORDER_IDS, user.getBiddingOrderIds());
		}
		if (null != user.getPurchaseOrderIds()) {
			bs.setSet(DatabaseTableConstants.COLUMN_USER_PURCHASED_ORDER_IDS, user.getPurchaseOrderIds());
		}
		return BaseDao.session.execute(bs);
	}

}
