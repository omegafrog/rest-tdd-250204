package com.example.rest_tdd.domain.post.post.dto;

import com.example.rest_tdd.domain.post.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostWithContentDto {

    private long id;
    @JsonProperty("createdDatetime")
    private LocalDateTime createdDate;
    @JsonProperty("modifiedDatetime")
    private LocalDateTime modifiedDate;
    private String title;
    private String content;
    private long authorId;
    private String authorName;
    private boolean opened;
    private boolean listed;

    public PostWithContentDto(Post post) {
        this.id = post.getId();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getNickname();
        this.opened = post.isOpened();
        this.listed = post.isListed();
    }
}
