package com.preemynence.csvimport.service.impl;

import com.preemynence.csvimport.service.ImportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ImportServiceImpl implements ImportService {
	@Override
	public Map<String, List<String>> saveBulkRecords(MultipartFile multipartFile) {
		return null;
	}
}
