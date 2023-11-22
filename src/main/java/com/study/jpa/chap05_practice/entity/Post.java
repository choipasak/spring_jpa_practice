package com.study.jpa.chap05_practice.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString(exclude = {"hashTags"})
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_post")
public class Post{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    private Long id;

    @Column(nullable = false)
    private String writer; // 작성자

    @Column(nullable = false)
    private String title; // 글 제목 (SNS같은 게시판은 title이 필요X)
    
    private String content; // 글 내용

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate; // 작성시간

    @UpdateTimestamp
    private  LocalDateTime updateDate; // 수정시간

    // orphanRemoval = true -> 고아 객체가 된 객체는 삭제 진행
    @OneToMany(mappedBy = "post", orphanRemoval = true) // 읽기전용 컬럼이라는 말도 포함된다.
    @Builder.Default // 특정 필드를 직접 지정한 값으로 초기화 하는 것을 강제하는 아노테이션
    // 밑의 hashTag는 @Builder로 초기화 된다.
    // @Builder.Default를 써주지 않으면 hashTags가 자동으로 null값으로 들어가기 때문에 반드시 작성 해 줘야 한다.
    // 내가 직접 초기화 값을 따로 만들었으니 초기화를 하지말라는 아노테이션이다.
    private List<HashTag> hashTags = new ArrayList<>(); // 초기화 필요

    // 양방향 매핑에서 리스트쪽에 데ㅣ터를 추가하는 편의 메서드 생성.
    public void addHashTag(HashTag hashTag){
        this.hashTags.add(hashTag); // 매개 값으로 전달 받은 HashTag 객체를 리스트에 추가.
        /*
        전달된 HasTag 객체가 가지고 있는 Post가
        이 메서드를 부르는 Post 객체와 주소 값이 서로 다르다면 데이터 불일치가 발생하기 때문에
        HashTag의 Post의 값도 이 객체로 변경.
        */
        if(this != hashTag.getPost()){
            hashTag.setPost(this);
        }
    }


}
