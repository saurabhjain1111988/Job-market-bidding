CREATE KEYSPACE job_market WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': '3'
};

USE job_market;

CREATE TABLE project (
  id int,
  title text,
  description text,
  budget double,
  fields map<text,text>,
  seller text,
  start_time timestamp,
  end_time timestamp,
  status text,
  soldout_amt double,
  bidOrder_Ids set<int>,
  current_bid_order text,
  lowest_bid_amt text,
  PRIMARY KEY (id)
);

CREATE TABLE bid_order (
  id int,
  user_id int,
  project set<text>,
  status text,
  bidding_time timestamp,
  bid_amt double,
  user_displayname text,
  PRIMARY KEY ((id, user_id))
);


CREATE TABLE purchase_order (
  id int,
  user_id int,
  purchased_projects set<text>,
  status text,
  purchase_time timestamp,
  purchase_amt double,
  user_displayname text,
  PRIMARY KEY ((id, user_id))
);

CREATE TABLE user (
  id int,
  displayname text,
  first_name text,
  last_name text,
  employer_name text,
  contact text,
  status text,
  PRIMARY KEY (id)
);

CREATE TABLE seller (
  id int,
  listed_project_ids set<int>,
  PRIMARY KEY (id)
);

CREATE TABLE buyer (
  id int,
  bidding_order_ids set<int>,
  bidding_order_ids set<int>,
  PRIMARY KEY (id)
);




