package com.example.rest_tdd.domain.post.post.service;

import com.example.rest_tdd.domain.member.member.entity.Member;
import com.example.rest_tdd.domain.post.post.entity.Post;
import com.example.rest_tdd.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content, boolean opened, boolean listed) {

        return postRepository.save(
                Post
                        .builder()
                        .author(author)
                        .title(title)
                        .content(content)
                        .opened(opened)
                        .listed(listed)
                        .build()
        );
    }

    public Page<Post> getItems(int currentPageNum, int pageSize) {
        Pageable pageable = PageRequest.of(currentPageNum, pageSize);
        return postRepository.findAllByListed(true, pageable);
    }

    public Page<Post> searchItemByTitle(int currentPageNum, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(currentPageNum, pageSize);
        return postRepository.findByListedAndTitleLike(true, "%"+keyword+"%", pageable);
    }
    public Page<Post> searchItemByContent(int currentPageNum, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(currentPageNum, pageSize);
        return postRepository.findByListedAndContentLike(true, "%"+keyword+"%", pageable);
    }
    public Optional<Post> getItem(long id) {
        return postRepository.findById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public Post modify(Post post, String title, String content, boolean opened, boolean listed) {
        post.setTitle(title);
        post.setContent(content);
        post.setOpened(opened);
        post.setListed(listed);
        return post;
    }

    public void flush() {
        postRepository.flush();
    }

    public long getTotalElements() {
        return postRepository.count();
    }
}
