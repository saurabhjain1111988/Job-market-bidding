package com.market.jobmarket.project.domain.project;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.market.jobmarket.project.domain.order.BidOrder;

public class Project extends ProjectTemplate {

	// Bidding Start time
	private Date bidStartTime;

	// Bidding End Time
	private Date bidEndTime;

	private ProjectStatus status;

	// Contains the Bidding Order Ids
	private Set<Integer> bidOrderIds;

	private double soldOutAmount;

	private BidOrder currentBidOrder;

	private double lowestBiddingAmount;

	public Date getBidStartTime() {
		return bidStartTime;
	}

	public void setBidStartTime(Date bidStartTime) {
		this.bidStartTime = bidStartTime;
	}

	public Date getBidEndTime() {
		return bidEndTime;
	}

	public void setBidEndTime(Date bidEndTime) {
		this.bidEndTime = bidEndTime;
	}

	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	public Set<Integer> getBidOrderIds() {
		if (null == this.bidOrderIds) {
			return new HashSet<Integer>();
		}
		return bidOrderIds;
	}

	public void setBidOrderIds(Set<Integer> bidOrderIds) {
		this.bidOrderIds = bidOrderIds;
	}

	public double getLowestBiddingAmount() {
		return lowestBiddingAmount;
	}

	public void setLowestBiddingAmount(double lowestBiddingAmount) {
		this.lowestBiddingAmount = lowestBiddingAmount;
	}

	public BidOrder getCurrentBidOrder() {
		return currentBidOrder;
	}

	public void setCurrentBidOrder(BidOrder currentBidOrder) {
		this.currentBidOrder = currentBidOrder;
	}

	public double getSoldOutAmount() {
		return soldOutAmount;
	}

	public void setSoldOutAmount(double soldOutAmount) {
		this.soldOutAmount = soldOutAmount;
	}

	public boolean isNewOrOpenForBidding() {
		return this.status.equals(ProjectStatus.NEW) || this.status.equals(ProjectStatus.OPEN_FOR_BIDDING);
	}

	public boolean isInValid() {
		return (StringUtils.isEmpty(this.getTitle()) || null == this.getSeller() || null == this.getSeller().getId()
				|| this.getSeller().getId() == 0);
	}

}
