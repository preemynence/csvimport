package com.preemynence.csvimport.service;

import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ImportService {
	List<Map<String, List<String>>> saveBulkRecords(MultipartFile multipartFile) throws SQLException;
}
