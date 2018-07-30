package com.preemynence.csvimport.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
public class Connections {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.driverClassName}")
	private String driverClass;

	@Value("${spring.datasource.username}")
	private String userName;

	@Value("${spring.datasource.password}")
	private String password;

	/**
	 * @return Connection Object of the Common DB.
	 */
	public Connection getCon() {
		Connection con = null;
		try {
			con = getCon(url, userName, password);
			return con;
		} catch (Exception e) {
			log.error("Error into database connectivity..!!", e);
		}
		return con;

	}

	/**
	 * @param database - DB name to which you want to connect
	 * @param username - username to connect to DB.
	 * @param password - Password to connect to DB.
	 * @return - Connection Object to the DB you passes parameters.
	 */
	public Connection getCon(String database, String username, String password) {
		Connection con = null;
		try {
			Class.forName(driverClass);
			con = DriverManager.getConnection(database, username, password);
			return con;
		} catch (Exception e) {
			log.error("Error into database connectivity..!!", e);
		}
		return con;

	}

	/**
	 * @param c   - Pass the connection Objects to close.
	 * @param st  - Pass the Statements Objects to close.
	 * @param rss - Pass the ResultSet Objects to close.
	 */
	public void closeConnections(final Connection c, final Statement st, final ResultSet... rss) {
		closeConnections(c);
		closeConnections(st, rss);
	}

	public void closeConnections(final Statement st, final ResultSet... rss) {
		for (final ResultSet rs : rss) {
			closeConnections(rs);
		}
		closeConnections(st);
	}

	public void closeConnections(final Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public void closeConnections(Connection c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void closeConnections(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}