package com.market.jobmarket.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.market.jobmarket.project.dao.DatabaseTableConstants;
import com.market.jobmarket.project.dao.UserDao;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.domain.user.Contact;
import com.market.jobmarket.project.domain.user.UserStatus;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.UserService;

@Component
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserDao userDao;

	@Override
	public void registerUser(BaseUser user) throws JobMarketException {
		if (null == user) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "user can't be empty");
		}
		user.setStatus(UserStatus.ACTIVE);
		try {
			userDao.upsertBaseUser(user);
		} catch (Exception ex) {
			logger.error("Exception occured while registering user " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public void updateUser(BaseUser user) throws JobMarketException {
		if (null == user || null == user.getId() || user.getId() == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "user or userId can't be empty");
		}
		try {
			userDao.upsertBaseUser(user);
		} catch (Exception ex) {
			logger.error("Exception occured while updating user " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}

	}

	@Override
	public boolean deleteUser(Integer userId) throws JobMarketException {
		if (null == userId || userId == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "userId can't be empty");
		}
		BaseUser user = getUser(userId);
		user.setStatus(UserStatus.INACTIVE);
		try {
			userDao.upsertBaseUser(user);
		} catch (Exception ex) {
			logger.error("Exception occured while deleting user " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		return true;
	}

	@Override
	public BaseUser getUser(Integer userId) throws JobMarketException {
		ResultSet resultSet = null;
		try {
			resultSet = userDao.selectByKey(userId);
			if (null == resultSet) {
				return null;
			}
		} catch (Exception ex) {
			logger.error("Exception occured while getting user " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		if (null == resultSet || null == resultSet.one()) {
			return null;
		}

		return getUserFromResultSetRow(resultSet.one());
	}

	private BaseUser getUserFromResultSetRow(Row row) {
		BaseUser user = new BaseUser();

		user.setId(row.getInt(DatabaseTableConstants.COLUMN_USER_ID));
		user.setDisplayName(row.getString(DatabaseTableConstants.COLUMN_USER_DISPLAY_NAME));
		user.setFirstName(row.getString(DatabaseTableConstants.COLUMN_USER_FIRST_NAME));
		user.setLastName(row.getString(DatabaseTableConstants.COLUMN_USER_LAST_NAME));
		user.setEmployerName(row.getString(DatabaseTableConstants.COLUMN_USER_EMPLOYER_NAME));
		user.setContact(row.get(DatabaseTableConstants.COLUMN_USER_CONTACT, Contact.class));
		user.setStatus(row.get(DatabaseTableConstants.COLUMN_USER_STATUS, UserStatus.class));

		return user;
	}

}
