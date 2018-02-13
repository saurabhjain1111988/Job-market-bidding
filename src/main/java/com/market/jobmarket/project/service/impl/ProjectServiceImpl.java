package com.market.jobmarket.project.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.market.jobmarket.project.dao.DatabaseTableConstants;
import com.market.jobmarket.project.dao.ProjectDao;
import com.market.jobmarket.project.domain.order.BidOrder;
import com.market.jobmarket.project.domain.order.BidStatus;
import com.market.jobmarket.project.domain.order.PurchaseOrder;
import com.market.jobmarket.project.domain.order.PurchaseStatus;
import com.market.jobmarket.project.domain.project.BaseProjectInfo;
import com.market.jobmarket.project.domain.project.Project;
import com.market.jobmarket.project.domain.project.ProjectStatus;
import com.market.jobmarket.project.domain.user.BaseUser;
import com.market.jobmarket.project.domain.user.Seller;
import com.market.jobmarket.project.exception.JobMarketException;
import com.market.jobmarket.project.exception.JobMarketExceptionReason;
import com.market.jobmarket.project.service.BiddingService;
import com.market.jobmarket.project.service.BuyerService;
import com.market.jobmarket.project.service.ProjectService;
import com.market.jobmarket.project.service.PurchaseService;
import com.market.jobmarket.project.service.SellerService;

@Component
public class ProjectServiceImpl implements ProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	ProjectDao projectDao;

	@Autowired
	SellerService sellerService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BiddingService biddingService;

	@Autowired
	PurchaseService purchaseService;

	@Override
	public void addProject(Project project) throws JobMarketException {
		if (null == project || null == project.getSeller() || null == project.getSeller().getId()
				|| project.getSeller().getId() == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData,
					"Project or seller Id can't be empty");
		}
		if (project.getBidEndTime().compareTo(new Date()) < 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData,
					"Project Bidding End Date should be greater than today");
		}

		Seller seller = sellerService.getSeller(project.getSeller().getId());
		if (seller == null) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, "Seller doesn't exist");
		}

		try {
			if (null == project.getBidStartTime()) {
				project.setBidStartTime(new Date());
			}
			if (null == project.getBidEndTime()) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(project.getBidStartTime());
				calendar.add(Calendar.MONTH, 1);
				project.setBidEndTime(calendar.getTime());
			}

			project.setStatus((project.getBidStartTime().compareTo(new Date()) < 0) ? ProjectStatus.OPEN_FOR_BIDDING
					: ProjectStatus.NEW);
			ResultSet resultSet = projectDao.upsert(project);
			if (null != resultSet && null != resultSet.one()) {

				project.setId(resultSet.one().getInt(DatabaseTableConstants.COLUMN_PROJECT_ID));

				if (CollectionUtils.isEmpty(seller.getListedProjectIds())) {
					seller.setListedProjectIds(new HashSet<Integer>());
				}
				seller.getListedProjectIds().add(project.getId());
				sellerService.updateSeller(seller);
			}
		} catch (Exception ex) {
			logger.error("Exception occured while adding the project " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public void updateProject(Project project) throws JobMarketException {
		if (null == project || null == project.getId() || project.getId() == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData,
					"Project or Project Id can't be empty");
		}
		try {
			projectDao.upsert(project);
		} catch (Exception ex) {
			logger.error("Exception occured while updating the project " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public void delete(Integer projectId) {
		if (projectId == null || projectId == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "ProjectId can't be empty");
		}
		Project project = getProject(projectId);
		if (null == project) {
			throw new JobMarketException(JobMarketExceptionReason.NotFound, "Project Not found");
		}
		project.setStatus(ProjectStatus.DELETED);
		project.setBidEndTime(new Date());
		try {
			project.getCurrentBidOrder().setStatus(BidStatus.PROJECT_CANCELLED);
			projectDao.upsert(project);
			updateBidOrdersStatus(project.getBidOrderIds(), BidStatus.PROJECT_CANCELLED);
		} catch (Exception ex) {
			logger.error("Exception occured while deleting the project " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
	}

	@Override
	public Project getProject(Integer projectId) {
		if (projectId == null || projectId == 0) {
			throw new JobMarketException(JobMarketExceptionReason.BadRequestData, "ProjectId can't be empty");
		}
		ResultSet resultSet = null;
		try {
			resultSet = projectDao.selectByKey(projectId);
		} catch (Exception ex) {
			logger.error("Exception occured while getting the project " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		if (null == resultSet || null == resultSet.one()) {
			return null;
		}
		return getProjectFromResultSetRow(resultSet.one());
	}

	@Override
	public List<Project> getAllOpenProjects() throws Exception {
		ResultSet resultSet = null;
		try {
			resultSet = projectDao.selectAll();
		} catch (Exception ex) {
			logger.error("Exception occured while getting all the projects " + ex);
			throw new JobMarketException(JobMarketExceptionReason.ProcessingError, ex);
		}
		if (null == resultSet || null == resultSet.all()) {
			return new ArrayList<Project>();
		}
		return filterOpenProjectsFromResultSet(resultSet.all());
	}

	@Override
	public boolean isProjectOpenToBid(Project project) throws JobMarketException {
		if (project.getBidEndTime().compareTo(new Date()) < 0 && project.isNewOrOpenForBidding()) {
			project.setSoldOutAmount(project.getCurrentBidOrder().getBidAmount());
			project.setStatus(ProjectStatus.SOLD_OUT);
			updateProject(project);
			purchaseService.placePurchaseOrder(getPurchaseOrder(project));
			if (null != project.getBidOrderIds()) {
				for (Integer bidOrderId : project.getBidOrderIds()) {
					if (!bidOrderId.equals(project.getCurrentBidOrder().getId())) {
						biddingService.updateBidStatus(bidOrderId, BidStatus.NOT_SELECTED);
					} else {
						biddingService.updateBidStatus(bidOrderId, BidStatus.ACCEPTED);
					}
				}
			}
			return false;
		}
		return true;
	}

	private void updateBidOrdersStatus(Set<Integer> bidOrderIds, BidStatus status) {
		for (Integer bidOrderId : bidOrderIds) {
			biddingService.updateBidStatus(bidOrderId, status);
		}
	}

	private PurchaseOrder getPurchaseOrder(Project project) {
		Set<BaseProjectInfo> projects = new HashSet<BaseProjectInfo>() {
			{
				BaseProjectInfo baseProjectInfo = new BaseProjectInfo();
				baseProjectInfo.setId(project.getId());
				baseProjectInfo.setTitle(project.getTitle());
			}
		};
		return new PurchaseOrder(projects, project.getCurrentBidOrder().getBidAmount(), PurchaseStatus.NEW,
				project.getCurrentBidOrder().getUserId(), project.getCurrentBidOrder().getUserDisplayName(),
				new Date());
	}

	private List<Project> filterOpenProjectsFromResultSet(List<Row> rows) throws Exception {
		List<Project> projects = new ArrayList<Project>();
		for (Row row : rows) {
			Project project = getProjectFromResultSetRow(row);
			if (project.getBidEndTime().compareTo(new Date()) > 0
					|| project.getStatus().equals(ProjectStatus.OPEN_FOR_BIDDING)) {
				projects.add(project);
			} else if (project.getStatus().equals(ProjectStatus.NEW)) {
				project.setStatus(ProjectStatus.EXPIRED_NO_BIDDING);
				projectDao.upsert(project);
			}
		}
		return projects;
	}

	private Project getProjectFromResultSetRow(Row row) {
		Project project = new Project();
		project.setId(row.getInt(DatabaseTableConstants.COLUMN_PROJECT_ID));
		project.setTitle(row.getString(DatabaseTableConstants.COLUMN_PROJECT_TITLE));
		project.setDescription(row.getString(DatabaseTableConstants.COLUMN_PROJECT_DESCRIPTION));
		project.setBudget(row.getDouble(DatabaseTableConstants.COLUMN_PROJECT_BUDGET));
		project.setProjectFields(row.getMap(DatabaseTableConstants.COLUMN_PROJECT_FIELDS, String.class, Object.class));
		project.setSeller(row.get(DatabaseTableConstants.COLUMN_SELLER, BaseUser.class));
		project.setBidStartTime(row.get(DatabaseTableConstants.COLUMN_PROJECT_BID_START_TIME, Date.class));
		project.setBidEndTime(row.get(DatabaseTableConstants.COLUMN_PROJECT_BID_END_TIME, Date.class));
		project.setStatus(row.get(DatabaseTableConstants.COLUMN_PROJECT_STATUS, ProjectStatus.class));
		project.setSoldOutAmount(row.getDouble(DatabaseTableConstants.COLUMN_PROJECT_SOLD_OUT_AMT));

		Set<Integer> bidOrderIds = row.getSet(DatabaseTableConstants.COLUMN_PROJECT_BIDDING_ORDER_IDS, Integer.class);
		Set<Integer> bidOrderIdsMutable = new HashSet<Integer>();
		bidOrderIdsMutable.addAll(bidOrderIds);
		project.setBidOrderIds(bidOrderIdsMutable);

		project.setCurrentBidOrder(row.get(DatabaseTableConstants.COLUMN_PROJECT_CURRENT_BID_ORDER, BidOrder.class));
		project.setLowestBiddingAmount(row.getDouble(DatabaseTableConstants.COLUMN_PROJECT_LOWEST_BIDDING_AMT));
		return project;

	}

}
