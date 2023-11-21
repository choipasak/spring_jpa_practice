package com.study.jpa.chap04_relation.repository;

import com.study.jpa.chap04_relation.entity.Department;
import com.study.jpa.chap04_relation.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;
    
    @Autowired
    DepartmentRepository departmentRepository;
    
//    @BeforeEach
    void bulkInsert(){
        // 부서와 사원 더미 데이터
        Department d1 = Department.builder()
                .name("영업부")
                .build();
        Department d2 = Department.builder()
                .name("개발부")
                .build();

        departmentRepository.save(d1);
        departmentRepository.save(d2);

        Employee e1 = Employee.builder()
                .name("라이옹")
                .department(d1)
                .build();
        Employee e2 = Employee.builder()
                .name("어피치")
                .department(d1)
                .build();
        Employee e3 = Employee.builder()
                .name("프로도")
                .department(d2)
                .build();
        Employee e4 = Employee.builder()
                .name("춘식이")
                .department(d2)
                .build();

        employeeRepository.save(e1);
        employeeRepository.save(e2);
        employeeRepository.save(e3);
        employeeRepository.save(e4);
    }

    @Test
    @DisplayName("특정 사원의 정보 조회")
    void testFindOne() {
        //given
        Long id = 2L;
        //when
//        Optional<Employee> employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("사원이 존재하지 않음!")
                );
        /*
        - Optional의 새로운 문법
        orElseThrow() : 예외가 터지면 에러를 띄워달라는 메서드
        */
        //then
        System.out.println("\n\n\n");
        System.out.println("employee = " + employee);
        System.out.println("\n\n\n");

        // employee에서 어피치라는 이름
        assertEquals("어피치", employee.getName());

        /*

        - 결과
        employee = Employee(id=2, name=어피치, department=Department(id=1, name=영업부))

        근데 sql문을 보면 select문이 없음.
        그럼 데이터를 어떻게 조회
        jpa는 이 안에서 이뤄지는 모든 것들을 (@Transactional 때문에) 하나의 트랜잭션으로 보기 때문에
        DB를 갔다오지 않고 context라는 공간에 기억을 해놨던 정보를 가져와서 보여준다.

        - 정리
        이 클래스 안의 모든 내용을 하나의 트랜잭션으로 관리하고 있었기 때문에(@Transactional 때문에) 방금 insert됬던 것을 기억했다가
        select를 하면 방금 insert했던 정보를 바로 가져와서(context라는 기억 공간) 보여 준다!
        */
    }

    @Test
    @DisplayName("부서 정보 조회")
    void testFindDept() {
        //given
        Long id = 1L;
        //when
        Department department = departmentRepository.findById(id)
                .orElseThrow();// 매개 값 X: 메시지는 X, 기본 값으로 런타임 예외를 띄운다
        //then
        System.out.println("\n\n\n");
        System.out.println("department = " + department);
        System.out.println("\n\n\n");

        // 결과
        // department = Department(id=1, name=영업부)
    }
    
}