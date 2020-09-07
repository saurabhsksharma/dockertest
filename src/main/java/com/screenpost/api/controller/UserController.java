package com.screenpost.api.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.screenpost.api.dto.Response;
import com.screenpost.api.dto.UserProfileDto;
import com.screenpost.api.service.UserService;
import com.screenpost.api.util.ApiException;

import io.swagger.annotations.Api;

@RestController
@Api(value="screenpost", description="User Service")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping(value = "/screenpost/user")
public class UserController {
	
	private Response response;
	
	@Autowired
	UserService userService;
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Response getAllUser() {

		List<UserProfileDto> userProfileDtoList = null;
		
		try {
			userProfileDtoList = userService.getAllUser();
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("User detail retrieved successfully");
			response.setResponseData(userProfileDtoList);
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
	@RequestMapping(value = "/{userid}", method = RequestMethod.GET)
	public Response getUser(@PathVariable("userid") int userId, SecurityContextHolder auth) {
		UserProfileDto userProfileDto = null;
		
		Collection<?extends GrantedAuthority> granted = auth.getContext().getAuthentication().getAuthorities();
		String role = granted.toArray()[0].toString();
		
		try {
			userProfileDto = userService.getUserById(userId, role);
			if(userProfileDto != null) {
				response = new Response();
				response.setError(false);
				response.setResponseCode(HttpServletResponse.SC_OK);
				response.setMessage("User detail retrieved successfully");
				response.setResponseData(userProfileDto);
			} else {
				throw new ApiException("User does not exist or accessible");
			}
		} catch (ApiException e) {
			response = new Response();
			response.setError(true);
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setMessage(e.getMessage());
			response.setResponseData(null);
		}
		
		return response;
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseEntity<?> createUser() {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{userid}", method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@PathVariable("userid") int userId) {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = "/{userid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("userid") int userId) {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}
}
