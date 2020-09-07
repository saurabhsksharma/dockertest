package com.screenpost.api.util;

public enum ContentType {
	
	// This will call enum constructor with one 
    // String argument 
	CHANNEL("channel"),
	PLAYLIST("playlist"),
	IMAGE("image"),
	LINK("link"),
	VIDEO("video");
	
	// declaring private variable for getting values 
    private String type; 
  
    // getter method 
    public String getType() 
    { 
        return this.type; 
    } 
  
    // enum constructor - cannot be public or protected 
    private ContentType(String type) 
    { 
        this.type = type; 
    } 
}
