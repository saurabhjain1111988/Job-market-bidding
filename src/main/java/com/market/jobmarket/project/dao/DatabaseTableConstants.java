package com.market.jobmarket.project.dao;

public class DatabaseTableConstants {

	public static final String TABLE_PROJECT="project";
	public static final String COLUMN_PROJECT_ID="id";
	public static final String COLUMN_PROJECT_TITLE="title";
	public static final String COLUMN_PROJECT_DESCRIPTION="description";
	public static final String COLUMN_PROJECT_BUDGET="budget";
	public static final String COLUMN_PROJECT_FIELDS="fields";
	public static final String COLUMN_SELLER="seller";
	public static final String COLUMN_PROJECT_BID_START_TIME="start_time";
	public static final String COLUMN_PROJECT_BID_END_TIME="end_time";
	public static final String COLUMN_PROJECT_STATUS="status";
	public static final String COLUMN_PROJECT_SOLD_OUT_AMT="soldout_amt";
	public static final String COLUMN_PROJECT_BIDDING_ORDER_IDS="bidOrder_Ids";
	public static final String COLUMN_PROJECT_CURRENT_BID_ORDER="current_bid_order";
	public static final String COLUMN_PROJECT_LOWEST_BIDDING_AMT="lowest_bid_amt";
	
	
	public static final String TABLE_BID_ORDER="bid_order";
	public static final String COLUMN_BID_ORDER_ID="id";
	public static final String COLUMN_BID_ORDER_USER_ID="user_id";
	public static final String COLUMN_BID_ORDER_PROJECT="project";
	public static final String COLUMN_BID_ORDER_STATUS="status";
	public static final String COLUMN_BID_ORDER_TIME="bidding_time";
	public static final String COLUMN_BID_ORDER_AMT="bid_amt";
	public static final String COLUMN_BID_ORDER_USER_DISPLAYNAME="user_displayname";
	
	
	public static final String TABLE_PURCHASE_ORDER="purchase_order";
	public static final String COLUMN_PURCHASE_ORDER_ID="id";
	public static final String COLUMN_PURCHASE_ORDER_USER_ID="user_id";
	public static final String COLUMN_PURCHASE_ORDER_PROJECTS="purchased_projects";
	public static final String COLUMN_PURCHASE_ORDER_STATUS="status";
	public static final String COLUMN_PURCHASE_ORDER_TIME="purchase_time";
	public static final String COLUMN_PURCHASE_ORDER_AMT="purchase_amt";
	public static final String COLUMN_PURCHASE_ORDER_USER_DISPLAYNAME="user_displayname";
	
	public static final String TABLE_USER="user";
	public static final String COLUMN_USER_ID="id";
	public static final String COLUMN_USER_DISPLAY_NAME="displayname";
	public static final String COLUMN_USER_FIRST_NAME="first_name";
	public static final String COLUMN_USER_LAST_NAME="last_name";
	public static final String COLUMN_USER_EMPLOYER_NAME="employer_name";
	public static final String COLUMN_USER_CONTACT="contact";
	public static final String COLUMN_USER_STATUS="status";
	
	public static final String TABLE_SELLER="seller";
	public static final String COLUMN_USER_LISTED_PROJECT_IDS="listed_project_ids";
	
	public static final String TABLE_BUYER="buyer";
	public static final String COLUMN_USER_BIDDING_ORDER_IDS="bidding_order_ids";
	public static final String COLUMN_USER_PURCHASED_ORDER_IDS="purchased_order_ids";
	
}

