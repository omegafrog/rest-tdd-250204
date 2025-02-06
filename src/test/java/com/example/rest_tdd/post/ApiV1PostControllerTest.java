package com.example.rest_tdd.post;

import com.example.rest_tdd.domain.member.member.controller.ApiV1MemberController;
import com.example.rest_tdd.domain.post.post.controller.ApiV1PostController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiV1PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("단건 조회")
    void getPost() throws Exception {
        Long id = 1L;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").exists());
    }
    @Test
    @DisplayName("단건 조회 - 없는 글 조회")
    void getPost2() throws Exception {
        Long id = -1L;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
        );

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 글입니다."));
    }
    @Test
    @DisplayName("단건 조회 - 비밀 글 작성자 아닌 사람이 조회")
    void getPost3() throws Exception {
        Long id = 3L;
        String apiKey = "user3";

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
                        .header("Authorization", "Bearer "+apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("비공개된 글입니다."));
    }
    private ResultActions writePost(String title, String content, String apiKey) throws Exception {
        return mvc.perform(
                post("/api/v1/posts")
                        .content("""
                                {
                                    "title": "%s",
                                    "content": "%s"
                                }
                                """.formatted(title, content)
                                .stripIndent()
                        )
                        .header("Authorization", "Bearer %s".formatted(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }
    @Test
    @DisplayName("글 작성")
    void writePost() throws Exception {
        String title = "title";
        String content = "content";
        String apiKey = "user1";

        ResultActions perform = writePost(title, content, apiKey);
        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글 작성이 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));
    }

    @Test
    @DisplayName("로그인 없이 글 작성")
    void writePostWithoutApiKey() throws Exception {
        String title = "title";
        String content = "content";
        String apiKey = "";

        ResultActions perform = writePost(title, content, apiKey);
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("잘못된 인증 정보입니다."));
    }
    @Test
    @DisplayName("body 없음")
    void writePostWithoutBody() throws Exception {
        String title = "";
        String content = "content";
        String apiKey = "user1";

        ResultActions perform = writePost(title, content, apiKey);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("title : Length : length must be between 3 and 2147483647\ntitle : NotBlank : must not be blank"));
    }
    private ResultActions modifyPost(Long postId, String title, String content, String apiKey) throws Exception {
        return mvc.perform(
                put("/api/v1/posts/%d".formatted(postId))
                        .content("""
                                {
                                    "title": "%s",
                                    "content": "%s"
                                }
                                """.formatted(title, content)
                                .stripIndent()
                        )
                        .header("Authorization", "Bearer %s".formatted(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }
    @Test
    @DisplayName("글 수정")
    void updatePost() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        String apiKey = "user1";
        ResultActions perform = modifyPost(postId, title, content, apiKey);
        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글 수정이 완료되었습니다.".formatted(postId)))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));
    }

    @Test
    @DisplayName("글 수정 - no apiKey")
    void updatePost2() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        String apiKey = "";
        ResultActions perform = modifyPost(postId, title, content, apiKey);
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("잘못된 인증 정보입니다."));
    }

    @Test
    @DisplayName("글 수정 - not owner ")
    void updatePost3() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        String apiKey = "user3";
        ResultActions perform = modifyPost(postId, title, content, apiKey);
        perform
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("자신이 작성한 글만 수정 가능합니다."));
    }
    @Test
    @DisplayName("글 수정 - no input data ")
    void updatePost4() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "";
        String apiKey = "user3";
        ResultActions perform = modifyPost(postId, title, content, apiKey);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("content : Length : length must be between 3 and 2147483647\ncontent : NotBlank : must not be blank"));
    }
    @Test
    @DisplayName("글 수정 - 관리자가 수정")
    void updatePost5() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "";
        String apiKey = "admin";
        ResultActions perform = modifyPost(postId, title, content, apiKey);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("content : Length : length must be between 3 and 2147483647\ncontent : NotBlank : must not be blank"));
    }
    private ResultActions deletePost(Long postId, String apiKey) throws Exception {
        return mvc
                .perform(
                        delete("/api/v1/posts/%d".formatted(postId))
                                .header("Authorization", "Bearer %s".formatted(apiKey))
                );
    }
    @Test
    @DisplayName("글 삭제")
    void deletePost() throws Exception {
        Long postId = 1L;
        String apiKey = "user1";
        ResultActions resultActions = deletePost(postId, apiKey);
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글 삭제가 완료되었습니다.".formatted(postId)));
    }
    @Test
    @DisplayName("글 삭제 - no apiKey")
    void deletePost2() throws Exception {
        Long postId = 1L;
        String apiKey = "";
        ResultActions resultActions = deletePost(postId, apiKey);
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("잘못된 인증 정보입니다."));
    }
    @Test
    @DisplayName("글 삭제 - not owner")
    void deletePost3() throws Exception {
        Long postId = 1L;
        String apiKey = "user2";
        ResultActions resultActions = deletePost(postId, apiKey);
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("자신이 작성한 글만 삭제 가능합니다."));
    }
}
