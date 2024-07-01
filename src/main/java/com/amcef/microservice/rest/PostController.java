package com.amcef.microservice.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amcef.microservice.dto.PostCreationDto;
import com.amcef.microservice.dto.PostResponseDto;
import com.amcef.microservice.dto.PostUpdateDto;
import com.amcef.microservice.exception.UserIdNotExistsException;
import com.amcef.microservice.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

	private PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}

	
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Integer id) {
    	Optional<PostResponseDto> postDto = postService.retrievePostById(id);
    	
    	if (postDto.isPresent()) {
    		return ResponseEntity.ok(postDto.get());
    	} else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
    @GetMapping(params = "userId")
    public ResponseEntity<List<PostResponseDto>> getPosts(@RequestParam Integer userId) {
    	List<PostResponseDto> posts = postService.retrievePostsByUserId(userId);
    	
    	if (posts.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	} else {
    		return ResponseEntity.ok(posts);
    	}
    }
    
    @PostMapping
    public ResponseEntity<String> addNewPost(@RequestBody PostCreationDto postDto) {
		try {
			int newPostId = postService.addNewPost(postDto);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
    				.buildAndExpand(newPostId).toUri();
    		return ResponseEntity.created(location).build();
		} catch (UserIdNotExistsException e) {
			return ResponseEntity.badRequest().body("Post creation unsuccessful! Invalid userId!");
		}
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Integer id, @RequestBody PostUpdateDto postDto) {
    	boolean isOk = postService.updatePost(id, postDto);
    	
    	if (isOk) {
    		return ResponseEntity.ok("Post updated successfully");
    	} else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Integer id) {
    	postService.deletePost(id);
    	
    	return ResponseEntity.noContent().build();
    }

}