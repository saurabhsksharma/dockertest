package com.screenpost.api.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.screenpost.api.dto.Response;
import com.screenpost.api.pojo.Screen;
import com.screenpost.api.pojo.ScreenContent;
import com.screenpost.api.service.ScreenService;
import com.screenpost.api.service.UserService;
import com.screenpost.api.util.ApiException;

import io.swagger.annotations.Api;

@RestController
@Api(value = "screenpost", description = "Screen Service")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.DELETE, RequestMethod.PUT })
@RequestMapping(value = "/screenpost/screen")
public class ScreenController {

	private static final Logger LOG = Logger.getLogger(ScreenController.class);

	private Response response;

	@Autowired
	ScreenService screenService;
	@Autowired
	UserService userService;

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Response getAllScreen(SecurityContextHolder auth) {

		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if (pricipal instanceof User) {
			try {
				userId = userService.getUserByUsername(((User) pricipal).getUsername()).getUserId();
			} catch (ApiException e) {
				LOG.error(e.getMessage());
				response = new Response();
				response.setError(false);
				response.setMessage(e.getMessage());
				response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
				response.setResponseData(null);
			}
		}

		List<Screen> screenList = null;

		try {
			screenList = screenService.getAllScreenByUser(userId);
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("Screen list retrieved successfully");
			response.setResponseData(screenList);
		} catch (ApiException e) {
			response = new Response();
			response.setError(true);
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setMessage(e.getMessage());
			response.setResponseData(null);
		}

		return response;
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{screenid}", method = RequestMethod.GET)
	public Response getScreen(@PathVariable("screenid") int screenId) {

		Screen screen = null;

		try {
			screen = screenService.getScreenById(screenId);
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("Screen list retrieved successfully");
			response.setResponseData(screen);
		} catch (ApiException e) {
			response = new Response();
			response.setError(true);
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setMessage(e.getMessage());
			response.setResponseData(null);
		}

		return response;
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public Response createScreen(@RequestParam("pincode") int pinCode, SecurityContextHolder auth) {

		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if (pricipal instanceof User) {
			try {
				userId = userService.getUserByUsername(((User) pricipal).getUsername()).getUserId();
			} catch (ApiException e) {
				LOG.error(e.getMessage());
				response = new Response();
				response.setError(true);
				response.setMessage(e.getMessage());
				response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
				response.setResponseData(null);
			}
		}

		Screen screen = null;

		try {
			screen = screenService.addNewScreen(pinCode, userId);
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("Screen added successfully");
			response.setResponseData(screen);
		} catch (ApiException e) {
			response = new Response();
			response.setError(true);
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setMessage(e.getMessage());
			response.setResponseData(null);
		}

		return response;
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{pincode}", method = RequestMethod.POST)
	public ResponseEntity<?> updateScreen(@PathVariable("pincode") int pinCode) {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{screenid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteScreen(@PathVariable("screenid") int screenId) {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/publish/{screenid}", method = RequestMethod.GET)
	public Response publish(@PathVariable("screenid") int screenId) {

		try {
			screenService.publishScreen(screenId);
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("Screen published successfully");
			response.setResponseData(null);
		} catch (ApiException | IOException e) {
			response = new Response();
			response.setError(true);
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setMessage(e.getMessage());
			response.setResponseData(null);
		}
		return response;
	}

}
