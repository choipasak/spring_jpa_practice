package com.study.jpa.chap05_practice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.jpa.chap05_practice.entity.HashTag;
import com.study.jpa.chap05_practice.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponseDTO {

    private String writer;
    private String title;
    private String content;
    private List<String> hashTags;


    // 이 클래스의 정보들은 화면단으로 JSON으로 보낼 정보들임. 쉽게 JSON으로 만들어 보내주는 방법이 있다 -> @JsonFormat
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime regDate;

    // 엔터티를 DTO로 변환하는 생성자
    public PostDetailResponseDTO(Post post){
        this.writer = post.getWriter();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.regDate = post.getCreateDate();
        this.hashTags = post.getHashTags() // 여기서 NPE가 터진것이였음 -> 빌더 아노테이션의 기본값을 설정하지 않아서 발생
                .stream()
                .map(HashTag::getTagName)
                .collect(Collectors.toList());
        /*
        HashTag 엔터티에서 태그 이름만 필요함
        그래서 이 클래스에서는 필드명을 List<String> 타입으로 받겠다고 선언했지만 엔터티는 List<HashTag>타입으로 리턴해줌
        타입을 맞춰주기 위해서 stream()을 사용해서 태그 이름만 뽑아주고 collect를 통해서 타입을 String으로 바꿔준다.
        */

    }
}
