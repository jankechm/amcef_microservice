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

import com.amcef.microservice.dto.GeneralResponseDto;
import com.amcef.microservice.dto.PostCreationDto;
import com.amcef.microservice.dto.PostResponseDto;
import com.amcef.microservice.dto.PostUpdateDto;
import com.amcef.microservice.exception.UserIdNotExistsException;
import com.amcef.microservice.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/posts")
public class PostController {

	private PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}

	@Operation(summary = "Get a post by its id")
	@ApiResponse(responseCode = "200", description = "OK",
		content = { @Content(mediaType = "application/json",
			schema = @Schema(implementation = PostResponseDto.class)) })
	@ApiResponse(responseCode = "404", description = "The post was not found.", content = @Content)
	@GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Integer id) {
    	Optional<PostResponseDto> postDto = this.postService.retrievePostById(id);
    	
    	if (postDto.isPresent()) {
    		return ResponseEntity.ok(postDto.get());
    	} else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
	@Operation(summary = "Get posts by userId")
	@ApiResponse(responseCode = "200", description = "OK",
		content = { @Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class))
		) })
	@ApiResponse(responseCode = "404", description = "No posts found with specified userId.", content = @Content)
    @GetMapping(params = "userId")
    public ResponseEntity<List<PostResponseDto>> getPosts(@RequestParam Integer userId) {
    	List<PostResponseDto> posts = this.postService.retrievePostsByUserId(userId);
    	
    	if (posts.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	} else {
    		return ResponseEntity.ok(posts);
    	}
    }
    
	@Operation(summary = "Add new post")
	@ApiResponse(responseCode = "201", description = "Post created.",
		content = { @Content(mediaType = "application/json",
			schema = @Schema(implementation = GeneralResponseDto.class)) })
	@ApiResponse(responseCode = "404", description = "Invalid userId.", content = @Content)
    @PostMapping
    public ResponseEntity<GeneralResponseDto> addNewPost(@RequestBody PostCreationDto postDto) {
		try {
			int newPostId = this.postService.addNewPost(postDto);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
    				.buildAndExpand(newPostId).toUri();
    		return ResponseEntity.created(location).body(new GeneralResponseDto("New post created successfully!"));
		} catch (UserIdNotExistsException e) {
			return ResponseEntity.badRequest().body(
					new GeneralResponseDto("Post creation unsuccessful! Invalid userId!"));
		}
    }
    
	@Operation(summary = "Update post")
	@ApiResponse(responseCode = "200", description = "Post updated successfully.",
		content = { @Content(mediaType = "application/json",
			schema = @Schema(implementation = GeneralResponseDto.class)) })
	@ApiResponse(responseCode = "404", description = "The post was not found.", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponseDto> updatePost(@PathVariable Integer id, @RequestBody PostUpdateDto postDto) {
    	boolean isOk = this.postService.updatePost(id, postDto);
    	
    	if (isOk) {
    		return ResponseEntity.ok(new GeneralResponseDto("Post updated successfully."));
    	} else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
	@Operation(summary = "Delete post")
	@ApiResponse(responseCode = "204", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
    	this.postService.deletePost(id);
    	
    	return ResponseEntity.noContent().build();
    }

}