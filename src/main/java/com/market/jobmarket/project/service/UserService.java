package com.market.jobmarket.project.service;

import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.exception.JobMarketException;

public interface UserService {

	public void registerUser(BaseUser user) throws JobMarketException;
	
	public void updateUser(BaseUser user) throws JobMarketException;
	
	public BaseUser getUser(Integer userId) throws JobMarketException;
	
	public boolean deleteUser(Integer userId) throws JobMarketException;
}
