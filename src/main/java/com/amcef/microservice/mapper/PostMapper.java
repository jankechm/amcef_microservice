package com.amcef.microservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.amcef.microservice.dto.PostCreationDto;
import com.amcef.microservice.dto.PostResponseDto;
import com.amcef.microservice.orm.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
	
	@Mapping(target = "id", ignore = true)
	Post toEntity(PostCreationDto dto);
	
	Post toEntity(PostResponseDto dto);
	
	PostResponseDto toResponseDto(Post entity);
}
