package com.screenpost.api.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.screenpost.api.dto.ScreenContentDto;
import com.screenpost.api.pojo.Screen;
import com.screenpost.api.pojo.ScreenContent;
import com.screenpost.api.service.dao.ScreenDao;
import com.screenpost.api.util.ApiException;

@Service
public class ScreenService {
	
	@Autowired
    ResourceLoader resourceLoader;
	
	@Autowired
	private ScreenDao screenDao;

	public List<Screen> getAllScreenByUser(int userId) throws ApiException {
		return screenDao.findAllByUserId(userId);
	}
	
	public Screen getScreenById(int screenId) throws ApiException {
		return screenDao.findById(screenId);
	}
	
	public Screen addNewScreen(int pinCode, int userId) throws ApiException {
		return screenDao.createByPinCode(pinCode, userId);
	}
	
	public ScreenContent addContentToScreen(int screenId, int contentId) throws ApiException {
		return screenDao.addContentToScreen(screenId, contentId);
	}
	
	public List<ScreenContentDto> getScreenContent(int screenId) throws ApiException {
		return screenDao.getScreenContent(screenId);
	}

	public void publishScreen(int screenId) throws ApiException, FileNotFoundException, IOException {
		Resource resource = resourceLoader.getResource("classpath:screen-post-firebase-adminsdk-adiet-460c1fe364.json");
		InputStream resourceAsStream = resource.getInputStream();

//		FileInputStream serviceAccount = new FileInputStream(resourceAsStream);

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

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("screen");
		
		Screen screen = this.getScreenById(screenId);
		
		DatabaseReference screenRef = ref.child(String.valueOf(screen.getPinCode()));
		
		screenRef.removeValueAsync();
		
		List<ScreenContentDto> screenContentList = screenDao.getScreenContent(screenId);
		
		screenRef.setValueAsync(screenContentList);
		
//		Map<String, Object> screenContentMap = new HashMap<>();
//		Map<String, Object> contentPropertyMap = null;
//		
//		for(ScreenContentDto screenContent : screenContentList) {
//			
//			contentPropertyMap = new HashMap<>();
//			contentPropertyMap.put("title", screenContent.getTitle());
//			contentPropertyMap.put("url", screenContent.getUrl());
//			contentPropertyMap.put("duration", screenContent.getDuration().toString());
//			contentPropertyMap.put("type", screenContent.getType());
//
//			screenContentMap.put(screenContent.getTitle().split("\\.")[0], contentPropertyMap);
//		}
//		
//		screenRef.setValueAsync(screenContentMap);
	}
}
