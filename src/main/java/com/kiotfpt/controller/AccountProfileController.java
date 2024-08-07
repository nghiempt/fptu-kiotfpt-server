package com.kiotfpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiotfpt.model.AccountProfile;
import com.kiotfpt.model.ResponseObject;
import com.kiotfpt.request.UpdatePasswordRequest;
import com.kiotfpt.service.AccountProfileService;

@CrossOrigin(origins = "http://localhost:8888")
@RestController
@RequestMapping("/v1/profile")
public class AccountProfileController {
	@Autowired
	private AccountProfileService service;

	@PreAuthorize("hasAuthority('user')")
	@GetMapping("")
	public ResponseEntity<ResponseObject> getProfileByAccountID() {
		return service.getProfileByAccountID();
	}

	@PreAuthorize("hasAuthority('user')")
	@PutMapping("/update")
	public ResponseEntity<ResponseObject> updateProfile(@RequestBody AccountProfile request) {
		return service.updateProfile(request);
	}

	@PreAuthorize("hasAuthority('user')")
	@PutMapping("/update-password")
	public ResponseEntity<ResponseObject> updatePassword(@RequestBody UpdatePasswordRequest request) {
		return service.updatePassword(request);
	}
}
