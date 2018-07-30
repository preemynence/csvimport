package com.preemynence.csvimport.service.impl;

import com.preemynence.csvimport.dao.Connections;
import com.preemynence.csvimport.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class ImportServiceImpl implements ImportService {

	@Autowired
	Connections connections;

	@Override
	public List<Map<String, List<String>>> saveBulkRecords(MultipartFile multipartFile) throws SQLException {
		log.info("Saving from file :" + multipartFile.getOriginalFilename());
		String tableName = multipartFile.getOriginalFilename();
		tableName = tableName.substring(0, tableName.lastIndexOf("."));
		log.info(tableName);
		Connection con = connections.getCon();

		Map metadata = getMetaData(con, tableName);

		System.out.print(metadata);

		connections.closeConnections(con);
		return null;
	}

	private Map<String, Map<String, Object>> getMetaData(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();

		Map<String, Map<String, Object>> result = new LinkedHashMap<>();

		ResultSet resultSet = metadata.getColumns(null, null, tableName, null);
		while (resultSet.next()) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("VALUE", null);
			obj.put("TYPE", resultSet.getString("TYPE_NAME"));
			result.put(resultSet.getString("COLUMN_NAME"), obj);
		}
		connection.close();
		return result;
	}


	private List<Map<String, Map<String, Object>>> getMapFromResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Map<String, Object>>> rows = new ArrayList<>();
		HashMap<String, Map<String, Object>> row;
		while (rs.next()) {
			row = new LinkedHashMap<>(columns);
			for (int i = 1; i <= columns; ++i) {
				String columnName = md.getColumnName(i);
				Map<String, Object> value = new LinkedHashMap<>();
				value.put("TYPE", md.getColumnTypeName(i));
				value.put("VALUE", rs.getObject(i));
				row.put(columnName, value);
			}
			rows.add(row);
		}
		if (rows.isEmpty()) {
			return null;
		} else {
			return rows;
		}
	}
}
