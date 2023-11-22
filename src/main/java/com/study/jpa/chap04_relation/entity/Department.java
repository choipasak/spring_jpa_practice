package com.study.jpa.chap04_relation.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString(exclude = {"employees"}) // 여러개 쓸거면 중괄호 작성, 여기서는 그냥 사용 해 줬음
@EqualsAndHashCode(of = "id") // id가 같으면 같은 개체로 인식하는 속성으로 설정
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_dept")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long id;

    @Column(name = "dept_name", nullable = false)
    private String name;

    // 양방향 매핑에서는 상대방 엔터티의 갱신에 관여할 수 없다.
    // 단순히 읽기 전용(조회)으로만 사용해야 한다. -> !!!!중요!!!!
    /*
    @OneToMany(mappedBy = "")
    mappedBy에는 상대방 엔터티의 조인되는 필드명을 작성.
    상대방 엔터티 클래스에서 작성되어 있는 이 클래스의 필드명 말하는 것임
    */
    @OneToMany(mappedBy = "department") // @OneToMany 의 이유: 부서는 하나고 사원은 여러명이니까
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
    // 이렇게 초기화가 반드시 필요하다! -> NPE 방지
    

}
