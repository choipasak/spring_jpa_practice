package com.study.jpa.chap02_querymethod.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Setter // 실무적 측면에서 setter는 신중하게 선택할 것.(객체의 무결성을 위해)
@Getter
@ToString @EqualsAndHashCode(of = "id")
// 필드명 id인 얘로 동등비교인지 확인하라는 속성(여러개면 ,치고 "" 사용해서 작성해주면 된다.)
// id(필드명)가 같으면 같은 객체로 인식해라. 라는 속성(of)
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "tbl_student")
public class Student {

    @Id
    @Column(name = "stu_id")
    @GeneratedValue(generator = "uid")
    @GenericGenerator(strategy = "uuid", name = "uid")
    private String id;

    @Column(name = "stu_name", nullable = false)
    private String name;

    private String city;

    private String major;

}
