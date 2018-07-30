package com.preemynence.csvimport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportData {

	@GetMapping("/test")
	public String test() {
		return "SUCCESS";
	}
}
