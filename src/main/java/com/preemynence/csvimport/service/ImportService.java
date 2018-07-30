package com.preemynence.csvimport.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImportService {
	Map<String, List<String>> saveBulkRecords(MultipartFile multipartFile);
}
