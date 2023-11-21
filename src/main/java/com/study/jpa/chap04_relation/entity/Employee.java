package com.study.jpa.chap04_relation.entity;

import lombok.*;

import javax.persistence.*;
@Setter @Getter
@ToString @EqualsAndHashCode(of = "id") // id가 같으면 같은 개체로 인식하는 속성으로 설정
@AllArgsConstructor @NoArgsConstructor
@Builder
@Entity // 얘는 테이블과 1:1로 매칭되는 클래스라고 알려주는 것임
@Table(name = "tbl_emp") // 이렇게 지정 안해주면 테이블이름은 클래스이름으로 지정된다.
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id;

    @Column(name = "emp_name", nullable = false) // not null 제약조건까지 달아줌
    private String name;

    // 여기가 FK가 된다.
    // 그래서 사원 입장에서는 자기가 속한 부서에 대한 정보를 가지고 있어야 한다.
    // 사원 : 부서 -> M : 1 이기 때문에 이 클래스(Employee)에 붙일 아노테이션은 @ManyToOne이 된다
    @ManyToOne
    @JoinColumn(name = "dept_id") // FK의 이름을 알려주는 아노테이션
    private Department department;
    // 어떤 데이터를 가져와야 하는지 타입으로 알려주면 가져옴

    


}
