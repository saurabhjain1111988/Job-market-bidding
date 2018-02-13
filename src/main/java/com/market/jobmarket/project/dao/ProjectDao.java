package com.market.jobmarket.project.dao;

import java.util.Date;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.domain.project.Project;
import com.market.jobmarket.project.domain.user.BaseUser;

public class ProjectDao extends BaseDao {

	private PreparedStatement upsert;
	private PreparedStatement selectByKey;
	private PreparedStatement selectAll;

	private String table = DatabaseTableConstants.TABLE_PROJECT;

	public ResultSet selectAll() throws Exception {
		if (null == this.selectAll) {
			this.selectAll = BaseDao.session.prepare(getSelectAll(table));
		}
		return BaseDao.session.execute(this.selectAll.bind());
	}

	public ResultSet selectByKey(Integer projectId) throws Exception {
		if (null == this.selectByKey) {
			this.selectByKey = BaseDao.session
					.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_PROJECT_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PROJECT_ID, projectId);
		return BaseDao.session.execute(bs);
	}

	public ResultSet selectByKeyProjectIdAndUserId(Integer projectId, Integer userId) throws Exception {
		if (null == this.selectByKey) {
			this.selectByKey = BaseDao.session
					.prepare(getSelectByKeyStatement(table, DatabaseTableConstants.COLUMN_PROJECT_ID));
		}
		BoundStatement bs = this.selectByKey.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PROJECT_ID, projectId);
		return BaseDao.session.execute(bs);
	}

	public ResultSet upsert(Project project) throws Exception {
		if (null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, DatabaseTableConstants.COLUMN_PROJECT_ID,
					DatabaseTableConstants.COLUMN_PROJECT_TITLE, DatabaseTableConstants.COLUMN_PROJECT_DESCRIPTION,
					DatabaseTableConstants.COLUMN_PROJECT_BUDGET, DatabaseTableConstants.COLUMN_PROJECT_FIELDS,
					DatabaseTableConstants.COLUMN_SELLER, DatabaseTableConstants.COLUMN_PROJECT_BID_START_TIME,
					DatabaseTableConstants.COLUMN_PROJECT_BID_END_TIME, DatabaseTableConstants.COLUMN_PROJECT_STATUS,
					DatabaseTableConstants.COLUMN_PROJECT_SOLD_OUT_AMT,
					DatabaseTableConstants.COLUMN_PROJECT_BIDDING_ORDER_IDS,
					DatabaseTableConstants.COLUMN_PROJECT_CURRENT_BID_ORDER,
					DatabaseTableConstants.COLUMN_PROJECT_LOWEST_BIDDING_AMT));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_PROJECT_ID, project.getId());
		bs.setString(DatabaseTableConstants.COLUMN_PROJECT_TITLE, project.getTitle());
		bs.setString(DatabaseTableConstants.COLUMN_PROJECT_DESCRIPTION, project.getDescription());
		bs.setDouble(DatabaseTableConstants.COLUMN_PROJECT_BUDGET, project.getBudget());
		if (null != project.getProjectFields()) {
			bs.setMap(DatabaseTableConstants.COLUMN_PROJECT_FIELDS, project.getProjectFields());
		}
		bs.set(DatabaseTableConstants.COLUMN_SELLER, project.getSeller(), BaseUser.class);
		bs.set(DatabaseTableConstants.COLUMN_PROJECT_BID_START_TIME, project.getBidStartTime(), Date.class);
		bs.set(DatabaseTableConstants.COLUMN_PROJECT_BID_END_TIME, project.getBidStartTime(), Date.class);
		if (null != project.getStatus()) {
			bs.setString(DatabaseTableConstants.COLUMN_PROJECT_STATUS, project.getStatus().toString());
		}
		bs.setDouble(DatabaseTableConstants.COLUMN_PROJECT_SOLD_OUT_AMT, project.getSoldOutAmount());
		if (null != project.getBidOrderIds()) {
			bs.setSet(DatabaseTableConstants.COLUMN_PROJECT_BIDDING_ORDER_IDS, project.getBidOrderIds());
		}
		bs.setDouble(DatabaseTableConstants.COLUMN_PROJECT_LOWEST_BIDDING_AMT, project.getLowestBiddingAmount());
		bs.set(DatabaseTableConstants.COLUMN_PROJECT_CURRENT_BID_ORDER, project.getCurrentBidOrder(), BidOrder.class);
		return BaseDao.session.execute(bs);
	}
}
