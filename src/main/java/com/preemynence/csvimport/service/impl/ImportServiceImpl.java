package com.preemynence.csvimport.service.impl;

import com.opencsv.CSVReader;
import com.preemynence.csvimport.dao.Connections;
import com.preemynence.csvimport.dao.DataTypes;
import com.preemynence.csvimport.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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

		List csvData = getCSVData(multipartFile);

		insertData(con, tableName, metadata, csvData);

		connections.closeConnections(con);
		return null;
	}

	private void insertData(Connection con, String tableName, Map<String, Map<String, Object>> metadata, List<Map<String, Object>> csvData) throws SQLException {
		PreparedStatement stmt = null;
		for (Map<String, Object> row : csvData) {
			StringBuilder columns = new StringBuilder();
			StringBuilder values = new StringBuilder();
			String query = "INSERT INTO " + tableName + " ";
			for (String key : metadata.keySet()) {
				DataTypes type = (DataTypes) metadata.get(key).get("TYPE");
				switch (type.isQuotesRequired()) {
					case "":
						System.out.println("Column : " + key);
						columns.append("," + key);
						System.out.println("Data : " + row.get(key));
						values.append("," + row.get(key));
						continue;
					case "'":
						System.out.println("Column : " + key);
						columns.append("," + key);
						System.out.println("Data : " + row.get(key));
						if (row.get(key) == null || row.get(key).equals("")) {
							values.append(",null");
						} else {
							values.append(",'" + row.get(key) + "'");
						}
						continue;
					default:
				}
			}
			query = query + "(" + columns.substring(1) + ") VALUES (" + values.substring(1) + ")";
			System.out.println(query);
			stmt = con.prepareStatement(query);
			stmt.executeUpdate();
		}
		if (stmt != null)
			stmt.close();
	}

	private List getCSVData(MultipartFile multipartFile) {
		try {
			BufferedReader br;
			CSVReader reader;
			String line;
			InputStream is = multipartFile.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			List<Map<String, Object>> result = new ArrayList<>();
			List<String> keys = new ArrayList<>();
			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				if (isFirst) {
					System.out.print("HEADER : ");
				} else {
					System.out.print("DATA : ");
				}
				reader = new CSVReader(new StringReader(line));
				String[] record;
				while ((record = reader.readNext()) != null) {
					Map<String, Object> data = new HashMap<>();
					if (isFirst) {
						for (int i = 0; i < record.length; i++) {
							keys.add(record[i].trim());
						}
					} else {
						for (int i = 0; i < record.length; i++) {
							data.put(keys.get(i), record[i].trim());
						}
						result.add(data);
					}
				}
				isFirst = false;
			}
			System.out.println(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Map<String, Object>> getMetaData(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();

		Map<String, Map<String, Object>> result = new LinkedHashMap<>();

		ResultSet resultSet = metadata.getColumns(null, null, tableName, null);
		while (resultSet.next()) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("VALUE", null);
			obj.put("TYPE", DataTypes.valueOf(resultSet.getString("TYPE_NAME").toUpperCase()));
			if (!resultSet.getString("COLUMN_NAME").equals("id"))
			result.put(resultSet.getString("COLUMN_NAME"), obj);
		}
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
