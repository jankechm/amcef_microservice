package com.amcef.microservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amcef.microservice.orm.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findAllByUserId(Integer userId);
}
