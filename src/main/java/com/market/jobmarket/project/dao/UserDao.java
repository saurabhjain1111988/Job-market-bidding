package com.market.jobmarket.project.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.domain.user.Contact;

public class UserDao extends BaseDao {

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

	public ResultSet upsertBaseUser(BaseUser user) throws Exception {
		if (null == this.upsert) {
			this.upsert = BaseDao.session.prepare(getUpsertStatement(table, DatabaseTableConstants.COLUMN_USER_ID,
					DatabaseTableConstants.COLUMN_USER_DISPLAY_NAME, DatabaseTableConstants.COLUMN_USER_FIRST_NAME,
					DatabaseTableConstants.COLUMN_USER_LAST_NAME, DatabaseTableConstants.COLUMN_USER_EMPLOYER_NAME,
					DatabaseTableConstants.COLUMN_USER_STATUS, DatabaseTableConstants.COLUMN_USER_CONTACT));
		}
		BoundStatement bs = this.upsert.bind();
		bs.setInt(DatabaseTableConstants.COLUMN_USER_ID, user.getId());
		bs.setString(DatabaseTableConstants.COLUMN_USER_DISPLAY_NAME, user.getDisplayName());
		bs.setString(DatabaseTableConstants.COLUMN_USER_FIRST_NAME, user.getFirstName());
		bs.setString(DatabaseTableConstants.COLUMN_USER_LAST_NAME, user.getLastName());
		bs.setString(DatabaseTableConstants.COLUMN_USER_EMPLOYER_NAME, user.getEmployerName());
		if (null != user.getStatus()) {
			bs.setString(DatabaseTableConstants.COLUMN_USER_STATUS, user.getStatus().toString());
		}
		if (null != user.getContact()) {
			bs.set(DatabaseTableConstants.COLUMN_USER_CONTACT, user.getContact(), Contact.class);
		}
		return BaseDao.session.execute(bs);
	}
}
