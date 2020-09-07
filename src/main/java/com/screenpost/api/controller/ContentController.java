package com.screenpost.api.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.services.drive.Drive;
import com.screenpost.api.dto.Response;
import com.screenpost.api.pojo.ContainerContent;
import com.screenpost.api.pojo.ContainerContentId;
import com.screenpost.api.pojo.Content;
import com.screenpost.api.pojo.ScreenContent;
import com.screenpost.api.service.ContentService;
import com.screenpost.api.service.ScreenService;
import com.screenpost.api.service.UserService;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.ContentType;

import io.swagger.annotations.Api;

@RestController
@Api(value="screenpost", description="Content Service")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping(value = "/screenpost/content")
public class ContentController {
	
	private static final Logger LOG = Logger.getLogger(ContentController.class);
	
	private Response response = null;
	private ContentType contentType = null;
	
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	@Autowired
	ScreenService screenService;
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Response getAllContent(SecurityContextHolder auth) {
		
		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if(pricipal instanceof User) {
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
		
		List<Content> contentList = null;
		
		try {
			contentList = contentService.getUserContent(userId, contentType);
			
			response = new Response();
			response.setError(false);
			response.setMessage("All content retrieved");
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setResponseData(contentList);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(false);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		return response;
	}
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{type}", method = RequestMethod.GET)
	public Response getAllContentByType(@PathVariable("type") String type, SecurityContextHolder auth) {
		
		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if(pricipal instanceof User) {
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
		
		List<Content> contentList = null;
		
		try {
			checkContentType(type);
			
			contentList = contentService.getUserContentByType(userId, contentType);
			
			response = new Response();
			response.setError(false);
			response.setMessage("All "+type+" retrieved");
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setResponseData(contentList);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(false);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		return response;
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{type}/{contentid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteContentByType(@PathVariable("type") String type, @PathVariable("contentid") int contentId) {
		return ResponseEntity.ok(HttpServletResponse.SC_OK);
	}
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Response uploadFile(@RequestParam("file") MultipartFile[] files, SecurityContextHolder auth) {
		if(files.length == 0) {
			response = new Response();
			response.setError(true);
			response.setMessage("No file for upload");
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
			return response;
		}
		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if(pricipal instanceof User) {
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
		
		List<Content> contentList = new ArrayList<Content>();
//		List<String> contentList = new ArrayList<String>();
		
		try {
			for(MultipartFile file: files) {
				checkContentType(file.getContentType());
				contentList.add(contentService.uploadFile(file, userId, contentType));
			}
			
			if(contentList.size() == files.length) {
				response = new Response();
				response.setError(false);
				response.setMessage("Contents saved.");
				response.setResponseCode(HttpServletResponse.SC_OK);
				response.setResponseData(contentList);
			} else {
				LOG.error("Some or all of the content not saved.");
				response = new Response();
				response.setError(true);
				response.setMessage("Some or all of the content not saved.");
				response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
				response.setResponseData(null);
			}
		} catch (ApiException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/addtoscreen", method = RequestMethod.POST)
	public Response addToScreen(@RequestParam("screenid") int screenId, @RequestParam("contentid") int contentId) {
		
		ScreenContent screenContent = null;
		
		try {
			screenContent = screenService.addContentToScreen(screenId, contentId);
			response = new Response();
			response.setError(false);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setMessage("Content added to Screen successfully");
			response.setResponseData(screenContent);
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
	@RequestMapping(value = "/addto{type}", method = RequestMethod.POST)
	public Response addToChannel(
			@PathVariable(name = "type") String type,
			@RequestParam(name = "id", required = false) Integer containerId,
			@RequestParam(name = "title", required = true) String title,
			@RequestParam(name = "contentid", required = true) Integer[] contentIds, 
			SecurityContextHolder auth) {
		
		try {
			checkContentType(type);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(true);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if(pricipal instanceof User) {
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
		
		List<ContainerContent> containerContentList = null;
		
		try {
			if(containerId != null && containerId != 0) {
				containerContentList = contentService.addContentInContainer(containerId, contentIds);
			} else {
				containerContentList = contentService.addContentInContainer(title, contentIds, this.contentType, userId);
			}
			
			if(containerContentList == null) {
				throw new ApiException("Error occurred while adding content in playlist");
			}
			
			response = new Response();
			response.setError(false);
			response.setMessage("Content added");
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setResponseData(null);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(false);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		return response;
	}
	
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@RequestMapping(value = "/{type}content/{id}", method = RequestMethod.GET)
	public Response getContentFromContainer(@PathVariable(name="type") String containerType,
			@PathVariable(name="id") Integer containerId,
			SecurityContextHolder auth) {
		
		try {
			checkContentType(containerType);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(true);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		Integer userId = null;
		Object pricipal = auth.getContext().getAuthentication().getPrincipal();
		if(pricipal instanceof User) {
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
		
		List<Content> contentList = null;
		
		try {
			contentList = contentService.getContentInContainer(containerId, userId);
			response = new Response();
			response.setError(false);
			response.setMessage("Content retrieved from "+containerType);
			response.setResponseCode(HttpServletResponse.SC_OK);
			response.setResponseData(contentList);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			response = new Response();
			response.setError(true);
			response.setMessage(e.getMessage());
			response.setResponseCode(HttpServletResponse.SC_EXPECTATION_FAILED);
			response.setResponseData(null);
		}
		
		return response;
	}

	private void checkContentType (String type) throws ApiException {
		
		if(type.equalsIgnoreCase("channel")) {
			contentType = ContentType.CHANNEL;
		} else if(type.equalsIgnoreCase("playlist")) {
			contentType = ContentType.PLAYLIST;
		} else if(type.equalsIgnoreCase("image") || type.matches("^(image)\\/(\\w*$)?")) {
			contentType = ContentType.IMAGE;
		} else if(type.equalsIgnoreCase("video") || type.matches("^(video)\\/(\\w*$)?")) {
			contentType = ContentType.VIDEO;
		} else {
			throw new ApiException("Invalid content type");
		}
	}
}
