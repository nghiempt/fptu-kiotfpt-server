package com.kiotfpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiotfpt.model.ResponseObject;
import com.kiotfpt.request.AddressRequest;
import com.kiotfpt.service.AddressService;

@CrossOrigin(origins = "http://localhost:8888")
@RestController
@RequestMapping(path = "/v1/address")
public class AddressController {

	@Autowired
	private AddressService service;

	@PreAuthorize("hasAuthority('user')")
	@GetMapping("/get-all")
	public ResponseEntity<ResponseObject> getAllAddressByAccountID() {
		return service.getAddressByAccountID();
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseObject> getAddressByID(@PathVariable int id) {
		return service.getAddressByID(id);
	}
	
	@PreAuthorize("hasAuthority('user')")
	@PostMapping("/create")
	public ResponseEntity<ResponseObject> createAddress(@RequestBody AddressRequest request) {
		return service.createAddress(request);
	}
	
	@PreAuthorize("hasAuthority('user')")
	@PutMapping("/update")
	public ResponseEntity<ResponseObject> updateAddress(@RequestBody AddressRequest request) {
		return service.updateAddress(request);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseObject> deleteAddress(@PathVariable int id) {
		return service.deleteAddress(id);
	}
	
	@GetMapping("/province/get-all")
	public ResponseEntity<ResponseObject> getAllProvince() {
		return service.getAllProvince();
	}
	
	@GetMapping("/district/get-all-by-province/{id}")
	public ResponseEntity<ResponseObject> getAllDistrictByProvinceID(@PathVariable int id) {
		return service.getAllDistrictByProvinceID(id);
	}
	
	@GetMapping("/set-default/{id}")
	public ResponseEntity<ResponseObject> setAddressDefaultByID(@PathVariable int id) {
		return service.setAddressDefaultByID(id);
	}
}
