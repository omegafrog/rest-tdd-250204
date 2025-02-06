package com.example.rest_tdd.domain.post.post.repository;

import com.example.rest_tdd.domain.member.member.entity.Member;
import com.example.rest_tdd.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByListed(boolean listed, Pageable pageable);

    Page<Post> findByListedAndTitleLike(boolean listed, String title, Pageable pageable);

    Page<Post> findByListedAndContentLike(boolean listed, String content, Pageable pageable);

    Page<Post> findByAuthorAndTitleLike(Member author, String title, Pageable pageable);

    Page<Post> findByAuthorAndContentLike(Member author, String content, Pageable pageable);

    Page<Post> findByAuthor(Member member, Pageable pageRequest);
}
