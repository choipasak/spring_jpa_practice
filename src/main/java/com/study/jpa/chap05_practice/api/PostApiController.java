package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.dto.PageDTO;
import com.study.jpa.chap05_practice.dto.PostListResponseDTO;
import com.study.jpa.chap05_practice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET , param: (page, size)
        게시물 개별 조회: /posts/{id}       - GET
        
        param으로 넘어오는 것은 쿼리스트링으로 값이 넘어오는 것
        {id}는 그냥 url에 묻어서 오는 것
        
        게시물 등록:     /posts            - POST
        게시물 수정:     /posts/{id}       - PATCH
        게시물 삭제:     /posts/{id}       - DELETE
     */
    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        // 화면단에게는 JSON형태로 전달이 될 것이다.
        PostListResponseDTO dto = postService.getPosts(pageDTO);

        return ResponseEntity.ok().body(dto);
        /*
        - ResponseEntity.ok()
        응답상태코드임. 200 상태를 리턴함
        */

    }




}
