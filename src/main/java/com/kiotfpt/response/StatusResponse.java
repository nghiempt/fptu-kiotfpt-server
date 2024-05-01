package com.kiotfpt.response;

import com.kiotfpt.model.Status;

public class StatusResponse {

	private int status_id;

	private String value;

	public StatusResponse() {
		super();
	}

	public StatusResponse(int status_id, String value) {
		super();
		this.status_id = status_id;
		this.value = value;
	}
	
	public StatusResponse(Status status) {
		super();
		this.status_id = status.getStatus_id();
		this.value = status.getValue();
	}

	public int getStatus_id() {
		return status_id;
	}

	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
