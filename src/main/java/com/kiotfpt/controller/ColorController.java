package com.kiotfpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiotfpt.model.ResponseObject;
import com.kiotfpt.service.ColorService;

@CrossOrigin(origins = "http://localhost:8888")
@RestController
@RequestMapping("/v1/color")
public class ColorController {

	@Autowired
	private ColorService service;

	@GetMapping("/get-all")
	public ResponseEntity<ResponseObject> getAllColors() {
		return service.getAllColors();
	}
}
