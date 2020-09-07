package com.screenpost.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.screenpost.api.pojo.ContainerContent;
import com.screenpost.api.pojo.ContainerContentId;
import com.screenpost.api.pojo.Content;
import com.screenpost.api.service.dao.ContentDao;
import com.screenpost.api.util.ApiException;
import com.screenpost.api.util.ContentType;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;

@Service
public class ContentService {
	
	@Autowired
    ResourceLoader resourceLoader;
	
	@Autowired
	ContentDao contentDao;
	
	private final Path rootLocation = Paths.get("upload-dir");

	public List<Content> getUserContent(Integer userId, ContentType contentType) throws ApiException {
		return contentDao.findAllContentByUser(userId);
	}

	public List<Content> getUserContentByType(int userId, ContentType type) throws ApiException {
		return contentDao.findAllContentByType(userId, type);
	}
	
	public List<ContainerContent> addContentInContainer(Integer containerId, Integer[] contentIds) throws ApiException {
		return contentDao.addContentInContainer(containerId, contentIds);
	}

	public List<ContainerContent> addContentInContainer(String title, Integer[] contentIds, ContentType type, int userId) throws ApiException {
		return contentDao.addContentInContainer(title, contentIds, type, userId);
	}
	
	public Content uploadFile(MultipartFile file, int userId, ContentType type) throws ApiException, IOException {
		
		/* if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				File dir = new File(rootLocation + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + file.getOriginalFilename());
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

//				logger.info("Server File Location="
//						+ serverFile.getAbsolutePath());

				return "You successfully uploaded file=" + file.getOriginalFilename();
			} catch (Exception e) {
				return "You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + file.getOriginalFilename()
					+ " because the file was empty.";
		} */

		Resource resource = resourceLoader.getResource("classpath:screen-post-firebase-adminsdk-adiet-460c1fe364.json");
		InputStream resourceAsStream = resource.getInputStream();

//		FileInputStream serviceAccount = new FileInputStream(resource.getFile());

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(resourceAsStream))
				.setDatabaseUrl("https://screen-post.firebaseio.com/")
				.build();

		FirebaseApp fireApp = null;
		if(!FirebaseApp.getApps().isEmpty()) {
			fireApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME);
		} else {
			fireApp = FirebaseApp.initializeApp(options);
		}

		Bucket bucket = StorageClient.getInstance(fireApp).bucket("screen-post.appspot.com");

		String blobString = "assets/" + file.getOriginalFilename();
		long duration = 100000;
		String blobType = file.getContentType();
		
		if(blobType.matches("^(video)\\/(\\w*$)?")) {
			try {
				Pattern r = Pattern.compile("^(video)(/)(\\w*)?");
				Matcher m = r.matcher(blobType);
				if(m.matches()) {
				  IMediaReader reader = ToolFactory.makeReader(file.getOriginalFilename());
				  IContainer container = reader.getContainer();
				  IContainerFormat format = IContainerFormat.make();
				  format.setInputFormat(m.group(3));
				  InputStream is = file.getInputStream();
				  
				  container.setInputBufferLength(is.available());
				  container.open(is, format, true, true);
				  
				  if(container.queryStreamMetaData() >= 0) { 
					  duration = container.getDuration()/1000;
				  }
				}
				 
			} catch (Exception e) {
				throw new ApiException(e.getMessage());
			}
		}

		Blob blob = bucket.create(blobString, file.getInputStream(), file.getContentType());
		
		if(blob != null) {
			return contentDao.saveContent(userId, type, blobString, file.getOriginalFilename(), duration);
		}

		return null; 
	}

	public List<Content> getContentInContainer(Integer containerId, Integer userId) throws ApiException {
		
		return contentDao.getContentInContainer(containerId, userId);
	}
	
}
