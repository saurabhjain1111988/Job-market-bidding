package com.market.jobmarket.project.domain.project;

import java.util.Map;

import com.market.jobmarket.project.domain.user.BaseUser;

public class ProjectTemplate extends BaseProjectInfo{

	private double budget;

	// To store project related Fields
	private Map<String, Object> projectFields;

	// Seller's basic Information need to be displayed for project.
	private BaseUser seller;

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

	public Map<String, Object> getProjectFields() {
		return projectFields;
	}

	public void setProjectFields(Map<String, Object> projectFields) {
		this.projectFields = projectFields;
	}

	public BaseUser getSeller() {
		return seller;
	}

	public void setSeller(BaseUser seller) {
		this.seller = seller;
	}

}
