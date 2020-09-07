package com.screenpost.api.dto;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "result")
public class Response {
	private boolean error;
	private int responseCode;
	private String message;
	private Object responseData;

	public Response() {
		// TODO
	}
	
	public Response(boolean error, String message) {
		this.error = error;
		this.message = message;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

}

