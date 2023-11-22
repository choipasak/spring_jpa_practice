package com.study.jpa.chap05_practice.service;

import com.study.jpa.chap05_practice.dto.PageDTO;
import com.study.jpa.chap05_practice.dto.PageResponseDTO;
import com.study.jpa.chap05_practice.dto.PostDetailResponseDTO;
import com.study.jpa.chap05_practice.dto.PostListResponseDTO;
import com.study.jpa.chap05_practice.entity.Post;
import com.study.jpa.chap05_practice.repository.HashTagRepository;
import com.study.jpa.chap05_practice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional // JPA 레파지토리는 트랜잭션 단위로 동작하기 때문에 반드시 작성 해 줘야 한다@
// JPA는 INSERT, UPDATE, DELETE시에 트랜잭션을 기준으로 동작하는 경우가 많음.
// 기능을 보장받기 위해서는 웬만하면 트랜잭션 기능을 함께 사용해야 합니다.
// 나중에 MVC구조를 잘 구조화 하면 Service에 아노테이션을 첨부하면 된다.
public class PostService {

    private final PostRepository postRepository;
    private final HashTagRepository hashTagRepository;

    public PostListResponseDTO getPosts(PageDTO pageDTO) {
        // DB에서 게시물 목록 가져오는 메서드

        // findAll에게 Pageable 타입을 주기 위해서 객체 선언 + 값 주입
        Pageable pageable = PageRequest.of(
                // zero-base 조심,
                pageDTO.getPage(),
                pageDTO.getSize(),
                Sort.by("createDate").descending() // 작성 일자를 기준으로 역 정렬
        );

        Page<Post> posts = postRepository.findAll(pageable);

        // 게시물 정보만 꺼내기
        List<Post> postList = posts.getContent();

        // 게시물 정보를 DTO의 형태에 맞게 변환(stream을 이용하여 객체마다 일괄 처리)
        List<PostDetailResponseDTO> detailList
                = postList
                .stream()
                .map(PostDetailResponseDTO::new)// 전달받은 값(post)을 그대로 dto의 생성자의 매개 값으로 보냈다.
                .collect(Collectors.toList());

        // DB에서 조회한 정보(게시글 리스트)를 JSON 형태에 맞는 DTO로 변환 -> PostListResponseDTO
        return PostListResponseDTO.builder()
                .count(detailList.size()) // 총 게시물 수가 아니라 조회된 게시물의 개수를 전달
                .pageInfo(new PageResponseDTO(posts)) // 페이지 정보가 담긴 객체를 DTO에게 전달해서 그쪽에서 처리하게 함.
                .posts(detailList)
                .build();

    }
}
