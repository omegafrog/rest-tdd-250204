package com.example.rest_tdd.domain.post.post.repository;

import com.example.rest_tdd.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByListed(boolean listed, Pageable pageable);

    Page<Post> findByListedAndTitleLike(boolean listed, String title, Pageable pageable);

    Page<Post> findByListedAndContentLike(boolean listed, String content, Pageable pageable);
}
