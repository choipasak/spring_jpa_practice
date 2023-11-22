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

    @OneToMany(mappedBy = "post")
    private List<HashTag> hashTags = new ArrayList<>(); // 초기화 필요

}
