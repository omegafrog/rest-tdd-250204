package com.example.rest_tdd.domain.post.post.controller;

import com.example.rest_tdd.domain.member.member.entity.Member;
import com.example.rest_tdd.domain.member.member.service.MemberService;
import com.example.rest_tdd.domain.post.post.dto.PostDto;
import com.example.rest_tdd.domain.post.post.entity.Post;
import com.example.rest_tdd.domain.post.post.service.PostService;
import com.example.rest_tdd.global.Rq;
import com.example.rest_tdd.global.dto.RsData;
import com.example.rest_tdd.global.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;
    private final Rq rq;
    private final MemberService memberService;

    @GetMapping
    public RsData<List<PostDto>> getItems() {

        List<Post> posts = postService.getItems();
        List<PostDto> postDtos = posts.stream()
                .map(PostDto::new)
                .toList();

        return new RsData<>(
                "200-1",
                "글 목록 조회가 완료되었습니다.",
                postDtos
        );
    }


    @GetMapping("{id}")
    public RsData<PostDto> getItem( @PathVariable long id) {

        Post post = postService.getItem(id)
                .orElseThrow(()->new ServiceException("404-1", "존재하지 않는 글입니다."));

        if(!post.isOpened()){
            Member member = rq.getAuthenticatedActor();
            post.canRead(member);
        }

        return new RsData<>(
                "200-1",
                "글 조회가 완료되었습니다.",
                new PostDto(post)
        );
    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(@PathVariable long id) {

        Member actor = rq.getAuthenticatedActor();
        Post post = postService.getItem(id).get();

        post.canDelete(actor);
        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글 삭제가 완료되었습니다.".formatted(id)
        );
    }


    record ModifyReqBody(@NotBlank @Length(min = 3) String title,
                         @NotBlank @Length(min = 3) String content
    ) {
    }

    @PutMapping("{id}")
    public RsData<PostDto> modify(@PathVariable long id, @RequestBody @Valid ModifyReqBody body
    ) {

        Member actor = rq.getAuthenticatedActor();
        Post post = postService.getItem(id).get();

        if (post.getAuthor().getId() != actor.getId()) {
            throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
        }

        post.canModify(actor);
        Post modify = postService.modify(post, body.title(), body.content());
        return new RsData<>(
                "200-1",
                "%d번 글 수정이 완료되었습니다.".formatted(id),
                new PostDto(modify)
        );
    }

    record WriteReqBody(
            @NotBlank @Length(min = 3) String title,
            @NotBlank @Length(min = 3) String content
    ) {
    }

    @PostMapping
    public RsData<PostDto> write(@RequestBody @Valid WriteReqBody body) {

        Member actor = rq.getAuthenticatedActor();
        Post post = postService.write(actor, body.title(), body.content(), true);

        return new RsData<>(
                "200-1",
                "글 작성이 완료되었습니다.",
                new PostDto(post)
        );
    }
}
