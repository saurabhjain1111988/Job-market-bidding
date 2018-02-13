package com.market.jobmarket.project.service;

import java.util.List;

import com.market.jobmarket.project.domain.project.Project;
import com.market.jobmarket.project.exception.JobMarketException;

public interface ProjectService {

	public void addProject(Project project) throws JobMarketException; 
	
	public void updateProject(Project project) throws JobMarketException;
	
	public void delete(Integer projectId) throws JobMarketException;
	
	public Project getProject(Integer projectId) throws JobMarketException;
	
	public List<Project> getAllOpenProjects() throws Exception;
	
	public boolean isProjectOpenToBid(Project project) throws JobMarketException;
	
}
