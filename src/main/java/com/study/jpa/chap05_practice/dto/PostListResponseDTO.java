package com.study.jpa.chap05_practice.dto;

import lombok.*;

import java.util.List;


@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder // 이렇게 작성 해 놓고 필요 없다면 지우기
public class PostListResponseDTO {
    // 응답할 정보를 담는 클래스
    
    private int count; // 총 게시물 수
    private PageResponseDTO pageInfo; // 페이지 렌더링 정보
    private List<PostDetailResponseDTO> posts; // 게시물 렌더링 정보

}
