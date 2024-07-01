package com.amcef.microservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.amcef.microservice.dto.PostCreationDto;
import com.amcef.microservice.dto.PostResponseDto;
import com.amcef.microservice.dto.PostUpdateDto;
import com.amcef.microservice.mapper.PostMapper;
import com.amcef.microservice.orm.Post;
import com.amcef.microservice.repository.PostRepository;

@Service
public class PostService {
	
private PostRepository postRepository;
    
    private PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }
    
    public Optional<PostResponseDto> retrievePostById(Integer id) {
    	Optional<Post> post = postRepository.findById(id);
    	
    	if (post.isEmpty()) {
    		// TODO search using external API
    		return Optional.empty();
    	}
    	else {
    		return Optional.of(postMapper.toResponseDto(post.get()));
    	}
    }
    
    public List<PostResponseDto> retrievePostsByUserId(Integer userId) {
    	List<Post> posts = postRepository.findAllByUserId(userId);
    	
    	return posts.stream().map(postMapper::toResponseDto).toList();
    }
    
    public Optional<Integer> addNewPost(PostCreationDto postDto) {
    	// TODO validate userId using external API
    	Post post = postMapper.toEntity(postDto);
    	Post resultPost = postRepository.save(post);
    	
    	return Optional.of(resultPost.getId());
    }
    
    public void deletePost(Integer id) {
    	postRepository.deleteById(id);
    }
    
    /**
     * 
     * @param id
     * @param postDto
     * @return true if successfully update, false if Post not found.
     */
    public boolean updatePost(Integer id, PostUpdateDto postDto) {
    	Optional<Post> postOpt = postRepository.findById(id);
    	
    	if (postOpt.isPresent()) {
    		Post post = postOpt.get();
    		
    		post.setTitle(postDto.getTitle());
    		post.setBody(postDto.getBody());
    		
    		postRepository.save(post);
    		
    		return true;
    	} else {
    		return false;
    	}
    }
}
