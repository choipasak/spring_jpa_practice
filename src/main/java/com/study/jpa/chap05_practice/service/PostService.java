package com.study.jpa.chap05_practice.service;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.entity.HashTag;
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

    public PostDetailResponseDTO getDetail(Long id) throws Exception {

        Post postEntity = getPost(id);


        return new PostDetailResponseDTO(postEntity);
    }



    public PostDetailResponseDTO insert(PostCreateDTO dto) throws Exception {

        // 게시물 저장
        Post saved = postRepository.save(dto.toEntity());
        // 저 toEntity()는 hashTags를 포함하지 않았음 하지만 hasTags를 넣어주는 것이 데이터의 일관성을 헤치지 않는다.
        // 따로 해시태그를 넣어주는 작업이 필요 -> private final HashTagRepository hashTagRepository; 를 사용해서 해시태그 처리를 해본다.

        // 해시태그 저장
        // 1. 해시태그 값 뽑기(DTO에서)
        List<String> hashTags = dto.getHashTags();
        if(hashTags != null && !hashTags.isEmpty()){
            hashTags.forEach(ht -> { // 이러면 list의 개수만큼 돌려지면서 정보가 저장된다.
                HashTag savedTag = hashTagRepository.save(
                        HashTag.builder()
                        .tagName(ht) // 문자열 tagName을 받는 필드명
                        // 그리고 이 해시태그가 달릴 게시글의 정보도 달라는 post라는 필드명도 있음
                        // 근데 마침 위에 save로 선언해 준 게시글의 정보를 담은 객체가 있음
                        .post(saved)
                        .build()
                );

                /*
                    # saved.addHashTag(savedTag); 를 작성 해 줘야 하는 이유

                    Post Entity는 DB에 save를 진행할 때 HashTag에 대한 내용을 갱신하지 않습니다.
                    HashTag Entity는 따로 save를 진행합니다. (테이블이 각각 나뉘어 있음)
                    HashTag는 양방향 맵핑이 되어있는 연관관계의 주인이기 때문에 save를 진행할 때 Post를 전달하므로
                    DB와 Entity와의 상태가 동일하지만,
                    Post는 HashTag의 정보가 비어있는 상태입니다.
                    Post Entity에 연관관계 편의 메서드를 작성하여 HashTag의 내용을 동기화 해야
                    추후에 진행되는 과정에서 문제가 발생하지 않습니다.
                    (Post를 화면단으로 return -> HashTag들도 같이 가야 함. -> 직접 갱신)
                    (Post를 다시 SELECT 해서 가져온다??? -> 의미없는 행동.(insert는 트랜잭션 종료 후 진행))
                */
                saved.addHashTag(savedTag);

            });
        }

        return new PostDetailResponseDTO(saved);
    }

    public PostDetailResponseDTO modify(PostModifyDTO dto) {

        // 수정 전 데이터 조회
        Post postEntity = getPost(dto.getPostNo()); // 메서드를 따로 뺀 getPost덕분에 1줄 댐

        // 수정 시작 (title, content 내용 바꿔주기)
        postEntity.setTitle(dto.getTitle());
        postEntity.setContent(dto.getContent());

        // 수정 완료 처리
        Post modifiedPost = postRepository.save(postEntity);

        return new PostDetailResponseDTO(modifiedPost);
    }


    // ctrl + alt + M : 메서드화 시켜준다.
    private Post getPost(Long id) {
        // 예외를 발생시키고 처리를 하지 않으면 서버거 멈추게된다. -> 뻥남
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "번 게시물이 존재하지 않습니다!"));
    }

    public void delete(Long id) throws Exception {
        postRepository.deleteById(id);
    }
}
