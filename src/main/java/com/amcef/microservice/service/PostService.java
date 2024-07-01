package com.amcef.microservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.amcef.microservice.dto.PostCreationDto;
import com.amcef.microservice.dto.PostResponseDto;
import com.amcef.microservice.dto.PostUpdateDto;
import com.amcef.microservice.exception.UserIdNotExistsException;
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
    		// search using external API and store in DB if found
    		Optional<PostResponseDto> postResponseDto = this.findPostInExternalApi(id);
    		if (postResponseDto.isPresent()) {
    			this.postRepository.save(this.postMapper.toEntity(postResponseDto.get()));
    		}
    		
    		return postResponseDto;
    	}
    	else {
    		return Optional.of(this.postMapper.toResponseDto(post.get()));
    	}
    }
    
    public List<PostResponseDto> retrievePostsByUserId(Integer userId) {
    	List<Post> posts = this.postRepository.findAllByUserId(userId);
    	
    	return posts.stream().map(this.postMapper::toResponseDto).toList();
    }
    
    public Integer addNewPost(PostCreationDto postDto) {
    	// validate userId using external API
    	boolean userIdExists = this.checkUserIdInExternalApi(postDto.getUserId());
    	if (!userIdExists) {
    		throw new UserIdNotExistsException();
    	}
    	
    	Post post = this.postMapper.toEntity(postDto);
    	Post resultPost = this.postRepository.save(post);
    	
    	return resultPost.getId();
    }
    
	public void deletePost(Integer id) {
    	this.postRepository.deleteById(id);
    }
    
    /**
     * 
     * @param id
     * @param postDto
     * @return true if successfully update, false if Post not found.
     */
    public boolean updatePost(Integer id, PostUpdateDto postDto) {
    	Optional<Post> postOpt = this.postRepository.findById(id);
    	
    	if (postOpt.isPresent()) {
    		Post post = postOpt.get();
    		
    		post.setTitle(postDto.getTitle());
    		post.setBody(postDto.getBody());
    		
    		this.postRepository.save(post);
    		
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private boolean checkUserIdInExternalApi(Integer userId) {
		boolean userIdExists = false;
    	
    	RestTemplate restTemplate = new RestTemplate();

    	String url = "https://jsonplaceholder.typicode.com/users/" + userId;
    	
    	try {
			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, null, Void.class);
			userIdExists = response.getStatusCode().is2xxSuccessful();
		} catch (RestClientException e) {
			userIdExists = false;
		}
    	
		return userIdExists;
	}
    
    private Optional<PostResponseDto> findPostInExternalApi(Integer id) {
    	RestTemplate restTemplate = new RestTemplate();

    	String url = "https://jsonplaceholder.typicode.com/posts/" + id;
    	
    	try {
			ResponseEntity<PostResponseDto> response = restTemplate.exchange(
					url, HttpMethod.GET, null, PostResponseDto.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return Optional.of(response.getBody());
			}
		} catch (RestClientException e) {
			return Optional.empty();
		}
    	
    	return Optional.empty();
	}
}
