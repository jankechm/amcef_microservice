package com.amcef.microservice.dto;

import lombok.Value;

@Value
public class PostResponseDto {
	
	private Integer id;
	
	private Integer userId;
    
    private String title;
    
    private String body;
}
