package com.amcef.microservice.dto;

import lombok.Value;

@Value
public class PostCreationDto {
    private Integer userId;
    
    private String title;
    
    private String body;
}
