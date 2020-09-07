package com.screenpost.api.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

@RestController 
@Api(value="screenpost", description="Test Service")
@ApiIgnore
@CrossOrigin(origins = "*", allowedHeaders = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping(value = "/screenpost")
public class TestApiController {
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<?> testApi() {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}
}