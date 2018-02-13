package com.market.jobmarket.project.dao;

import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.BoundStatement;

public class BaseDao {

	// Reusing Purpose
	protected ObjectMapper jsonMapper = new ObjectMapper();

	// Application settings
	protected static String server_ip = "127.0.0.1";
	protected static String keyspace = "sampleDB";

	// Application connection objects
	protected static Cluster cluster = null;
	protected static Session session = null;

	/**
	 * Builds Insert / Update statements for a given table and columns
	 *
	 * @param table
	 * @param columns
	 * @return Query String
	 */
	protected String getUpsertStatement(String table, String... columns) {
		Object[] values = new Object[columns.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = QueryBuilder.bindMarker();
		}
		return QueryBuilder.insertInto(keyspace, table).values(columns, values).getQueryString();
	}

	/**
	 * Builds CQL statements to select from a given table, filtered by keys
	 *
	 * @param table
	 * @param keys
	 * @return Query String
	 * @throws Exception
	 */
	protected String getSelectByKeyStatement(String table, String... keys) throws Exception {
		if (keys == null || keys.length <= 0) {
			throw new Exception("Null or Empty where clause");

		}
		Select.Where where = QueryBuilder.select().all().from(keyspace, table)
				.where(QueryBuilder.eq(keys[0], QueryBuilder.bindMarker()));
		for (int i = 1; i < keys.length; i++) {
			where.and(QueryBuilder.eq(keys[i], QueryBuilder.bindMarker()));
		}
		return where.getQueryString();
	}
	
	/**
	 * Builds CQL statements to select from a given table, filtered by keys
	 *
	 * @param table
	 * @return Query String
	 */
	protected String getSelectAll(String table){
		 return QueryBuilder.select().all().from(keyspace, table).getQueryString();
	}

	/**
	 * Builds CQL delete statement for a given table, filtered by keys
	 *
	 * @param table
	 * @param keys
	 * @return
	 */
	protected String getDeleteByKeyStatement(String table, String... keys) {
		Delete.Where where = QueryBuilder.delete().from(keyspace, table)
				.where(QueryBuilder.eq(keys[0], QueryBuilder.bindMarker()));
		for (int i = 1; i < keys.length; i++) {
			where.and(QueryBuilder.eq(keys[i], QueryBuilder.bindMarker()));
		}
		return where.getQueryString();
	}

	/**
	 * Prepare statement and set consistency level
	 *
	 * @param query
	 * @return PreparedStatement
	 */
	public PreparedStatement prepareStmt(String query) {
		return session.prepare(query);
	}

	/**
	 * Performs execute on the provided BoundStatement
	 * 
	 * @param boundStatement
	 * @return
	 * @throws Exception
	 */
	public ResultSet execute(BoundStatement boundStatement) throws Exception {
		if (boundStatement == null) {
			throw new Exception("A valid non-null BoundStatement is required.");
		}
		return session.execute(boundStatement);
	}

	public static void openConnection() {
		if (cluster != null)
			return;
		cluster = Cluster.builder().addContactPoints(server_ip).build();
		session = cluster.connect(keyspace);
	}

	public static void closeConnection() {
		if (cluster != null) {
			cluster.close();
			cluster = null;
			session = null;
		}
	}

}
