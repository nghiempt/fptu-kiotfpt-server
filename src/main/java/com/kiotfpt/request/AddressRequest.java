package com.kiotfpt.request;

import com.kiotfpt.model.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

	private int account_profile_id;
	private String address_value;
	private int district_id;
	private int province_id;
	
	public AddressRequest(Address address) {
		super();
		this.account_profile_id = address.getProfile().getId();
		this.address_value = address.getValue();
		this.district_id = address.getDistrict().getId();
		this.province_id = address.getProvince().getId();
	}

}
