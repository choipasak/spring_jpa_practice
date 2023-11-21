package com.study.jpa.chap04_relation.repository;

import com.study.jpa.chap04_relation.entity.Department;
import com.study.jpa.chap04_relation.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class DepartmentRepositoryTest {

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    // EntityManager가 영속성 컨텍스트를 관리하는 개체
    // 엔터티들을 관리하는 역할을 수행하는 클래스(영속성 컨텍스트를 관리함, Spring Data JPA에서만 사용하는 게 X)
    // 영속성 컨텍스트(조회한 내용을 담는 박스라고 생각하면 된다)의 내의 내용들을 DB에 반영시키거나 비워내거나 수명을 관리할 수 있는 객체.
    // 강제성을 주장할 수 있어짐. 특정 시점에서 DB에 커밋하라고 명령 내리기 가능해짐
    @Autowired
    EntityManager entityManager;
    
    @Test
    @DisplayName("특정 부서를 조회하면 해당 부서원들도 함께 조회되어야 한다.")
    void testFindDept() {
        //given
        Long id = 2L;
        
        //when
        Department department = departmentRepository.findById(id)
                .orElseThrow();// 부서가 조회가 되지 않으면 에러를 발생 시켜라
        
        //then
        System.out.println("\n\n\n");
        System.out.println("department = " + department);
        System.out.println("department.getEmployees() = " + department.getEmployees());
        System.out.println("\n\n\n");
        /*
        이러면 에러가 발생함(StackOverflowError)
        이유: 무한 순환 참조가 이뤄지고 있어서 에러가 발생함 -> 서로가 서로를 계속 부르고 있다는 말임
        해결: toString()에서 Department를 빼달라고 해주면 된다.
            jpa 연관관계 매핑에서 연관관계 데이터는 toString에서 제외해야 합니다.
            @ToString(exclude = {"department"}) -> Employee.java 파일
            @ToString(exclude = {"Employee"}) -> Department.java 파일
        ! 여러개 쓸거면 중괄호 작성, 여기서는 그냥 사용 해 줬음
        */

        /*
        결과
        department = Department(id=2, name=개발부, employees=[Employee(id=3, name=프로도), Employee(id=4, name=춘식이)])
        department.getEmployees() = [Employee(id=3, name=프로도), Employee(id=4, name=춘식이)]


        select
        employees0_.dept_id as dept_id3_1_0_,
        employees0_.emp_id as emp_id1_1_0_,
        employees0_.emp_id as emp_id1_1_1_,
        employees0_.dept_id as dept_id3_1_1_,
        employees0_.emp_name as emp_name2_1_1_
        from
            tbl_emp employees0_
        where
            employees0_.dept_id=2

        */
    }
    
    @Test
    @DisplayName("Lazy로딩과 Eager로딩의 차이")
    void testLazyEager() {
        // 3번 사원의 정보를 조회하고 싶은데 부서 정보까진 필요없다 = JOIN이 필요하지 않다

        //given
        Long id = 3L;

        //when
        Employee employee = employeeRepository.findById(id)
                .orElseThrow();
        //then
        System.out.println("\n\n\n");
        System.out.println("employee = " + employee);
        System.out.println("dept_name = " + employee.getDepartment().getName());
        System.out.println("\n\n\n");

        /*
        - 결과
        employee = Employee(id=3, name=프로도)

        - 조회문
        select
        employee0_.emp_id as emp_id1_1_0_,
        employee0_.dept_id as dept_id3_1_0_,
        employee0_.emp_name as emp_name2_1_0_,
        department1_.dept_id as dept_id1_0_1_,
        department1_.dept_name as dept_nam2_0_1_
        from
            tbl_emp employee0_
        left outer join
            tbl_dept department1_
                on employee0_.dept_id=department1_.dept_id
        where
            employee0_.emp_id=3

        => JOIN이 필요가 없는데 JOIN을 해서 데이터를 가져온 것을 볼 수 있음

        - 해결: @ManyToOne(fetch = FetchType.LAZY) -> Department.java 파일
        Eager: 항상 무조건 조인을 수행
        Lazy: 필요한 경우에만 조인을 수행. (실무에선 Lazy를 더 많이 사용)

        - 해결 후 조회문
        select
        employee0_.emp_id as emp_id1_1_0_,
        employee0_.dept_id as dept_id3_1_0_,
        employee0_.emp_name as emp_name2_1_0_
        from
            tbl_emp employee0_
        where
            employee0_.emp_id=3
        => 어떻게든 JOIN을 안하는 결과를 볼 수 있음

        - 결과: dept_name = 개발부

        */
    }

    @Test
    @DisplayName("양방향 연관 관계에서 연관 데이터의 수정")
    void testChangeDept() {
        // 3번 사원의 부서를 2번 부서에서 1번 부서로 변경해야 한다.

        //given
        //when
        Employee foundEmp = employeeRepository.findById(3L)
                .orElseThrow();
        // Employee는 Department 객체 자체가 필요함
        Department newDept = departmentRepository.findById(1L)
                .orElseThrow();

        // 2번(권장) -> 사원의 부서 정보를 업데이트 하면서, 부서에 대한 저오도 같이 업데이트.
        foundEmp.setDepartment(newDept);
        newDept.getEmployees().add(foundEmp); // 양방향으로 서로에게 정보를 추가해 준 것이다.
        // 새로운 부서로 수정(Update) 완.

        employeeRepository.save(foundEmp);




        /*
        1번 -> 변경 감지(더티 체크) 후 변경된 내용을 DB에 즉시 반영하는 역할.
        entityManager.flush(); // DB로 밀어내기
        entityManager.close(); // 영속성 컨텍스트 비우기 (비우지 않으면 컨텍스트 내의 정보를 참조하려 함: 효율적으로 로직을 돌리려는 특징 때문)
        */




        /*
        수정문이 들어간 것을 볼 수 있음!
        update
                tbl_emp
        set
                dept_id=1,
                emp_name='프로도'
        where
                emp_id=3
        */

        // 1번 부서 정보를 조회해서 모든 사원을 보겠다.
        Department foundDept = departmentRepository.findById(1L).orElseThrow();

        //then
        System.out.println("\n\n\n");
        foundDept.getEmployees().forEach(System.out::println);
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("N+1 문제 발생 예시")
    void testNPlus1Ex() {
        //given
        List<Department> departments = departmentRepository.findAll();
        //when

        //then
        departments.forEach(dept -> {
            System.out.println("\n\n=============== 사원 리스트 ===============");
            List<Employee> employees = dept.getEmployees();
            System.out.println(employees);
            System.out.println("\n\n");
        });
    }

    @Test
    @DisplayName("N+1 문제 발생 예시")
    void testNPlus1Solution() {
        //given
        List<Department> departments = departmentRepository.findAllIncludesEmployees();
        //when

        //then
        departments.forEach(dept -> {
            System.out.println("\n\n=============== 사원 리스트 ===============");
            List<Employee> employees = dept.getEmployees();
            System.out.println(employees);
            System.out.println("\n\n");
        });
        /*
        - 조인문이 출력된 것을 볼 수 있다.
        select
            department0_.dept_id as dept_id1_0_0_,
                    employees1_.emp_id as emp_id1_1_1_,
            department0_.dept_name as dept_nam2_0_0_,
                    employees1_.dept_id as dept_id3_1_1_,
            employees1_.emp_name as emp_name2_1_1_,
                    employees1_.dept_id as dept_id3_1_0__,
            employees1_.emp_id as emp_id1_1_0__
        from
            tbl_dept department0_
        inner join
            tbl_emp employees1_
        on department0_.dept_id=employees1_.dept_id
        */

    }

}