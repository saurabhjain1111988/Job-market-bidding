package com.market.jobmarket.project.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.user.Seller;

public class SellerDao extends BaseDao {
	private PreparedStatement upsert;
	private PreparedStatement selectByKey;

	private String table = DatabaseTableConstants.TABLE_USER;

	public ResultSet selectByKey(Integer userId) throws Exception {
		if (null == this.selectByKey) {
			this.selectByKey = BaseDao.session
					.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_USER_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_USER_ID, userId);
		return BaseDao.session.execute(bs);
	}

	public ResultSet upsertSeller(Seller user) throws Exception {
		if (null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, DatabaseTableConstants.COLUMN_USER_ID,
					DatabaseTableConstants.COLUMN_USER_LISTED_PROJECT_IDS));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_USER_ID, user.getUserId());
		if (null != user.getListedProjectIds()) {
			bs.setSet(DatabaseTableConstants.COLUMN_USER_LISTED_PROJECT_IDS, user.getListedProjectIds());
		} 
		return BaseDao.session.execute(bs);
	}
}
