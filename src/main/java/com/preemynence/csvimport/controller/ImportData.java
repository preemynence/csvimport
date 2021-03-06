package com.preemynence.csvimport.controller;

import com.preemynence.csvimport.service.ImportService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@Slf4j
public class ImportData {

	@Autowired
	private ImportService importService;

	@GetMapping("/test")
	public String test() {
		return "SUCCESS";
	}

	@ApiOperation(value = "Bulk import.", response = ResponseEntity.class, notes = "The file name is the table name , first row should be of columns names and rest of the rows as data.")
	@PostMapping(value = "/bulkImport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map> bulkImport(@RequestPart MultipartFile multipartFile) throws IOException, SQLException {
		return new ResponseEntity(importService.saveBulkRecords(multipartFile), HttpStatus.OK);
	}

}
