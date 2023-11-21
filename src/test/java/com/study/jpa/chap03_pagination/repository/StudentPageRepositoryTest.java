package com.study.jpa.chap03_pagination.repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// JPA는 INSERT, UPDATE, DELETE시에 트랜잭션을 기준으로 동작하는 경우가 많음.
// 기능을 보장받기 위해서는 웬만하면 트랜잭션 기능을 함께 사용해야 합니다.
// 나중에 MVC구조를 잘 구조화 하면 Service에 아노테이션을 첨부하면 된다.
@Transactional
@Rollback(false)
class StudentPageRepositoryTest {

    @Autowired
    StudentPageRepository pageRepository;

    /*@BeforeEach
    void bulkInsert(){
        // 학생을 147명 저장
        for (int i = 0; i <= 147 ; i++) {
            Student s = Student.builder()
                    .name("김테스트" + i)
                    .city("도시" + i)
                    .major("전공" + i)
                    .build();
            pageRepository.save(s);
        }
    }*/

    @Test
    @DisplayName("기본 페이징 테스트")
    void testBasicPagination() {
        //given
        // 현재 사용자가 보는 페이지 & 한 페이지에 보여지는 이름 개수
        int pageNo = 1;
        int amount = 10;

        // 페이지 정보 생성
        // 페이지 번호가 zero-based -> 0이 1페이지
        Pageable pageInfo = PageRequest.of(pageNo-1, amount,
                Sort.by(
                        Sort.Order.desc("name"),
                        Sort.Order.asc("city") // 이렇게도 작성이 가능하다.
                ));
        /*
        select
        student0_.stu_id as stu_id1_1_,
                student0_.city as city2_1_,
        student0_.major as major3_1_,
                student0_.stu_name as stu_name4_1_
        from
        tbl_student student0_
        order by
        student0_.stu_name desc,
        student0_.city asc limit 10
        - 이렇게 자동으로 desc과 asc가 들어감.
        */

        // 상속받는 부모가 pageable을 가지고 있어서 당연히 자식도 사용 가능함.
        /*Sort.by()
        정렬 기준은 엔터티 클래스의 필드명으로 작성 해 줘야 한다. -> Sort.by("name")
        내림차로 정렬하고 싶다면 뒤에 Sort.by("name").descending() 해주면 된다
        */

        //when
        Page<Student> students = pageRepository.findAll(pageInfo);

        // 페이징이 완료된 총 학생 데이터 묶음
        List<Student> studentList = students.getContent();// 학생들 정보만 얻어 낼 수 있음

        // 총 페이지 수
        int totalPages = students.getTotalPages();
        long totalElements = students.getTotalElements();
        boolean next = students.hasNext();
        boolean prev = students.hasPrevious();
        // 페이징에 관련 된 여러가지 정보들을 얻을 수 있다.

        //then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("next = " + next);
        System.out.println("prev = " + prev);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");
        }
        
        @Test
        @DisplayName("이름 검색 + 페이징")
        void testSearchAndPaging() {
            //given
            int pageNo = 5;
            int size = 10;
            Pageable pageInfo = PageRequest.of(pageNo - 1, size);
            //when
            Page<Student> students = pageRepository.findByNameContaining("3", pageInfo);

            int totalPages = students.getTotalPages();
            long totalElements = students.getTotalElements();
            boolean next = students.hasNext();
            boolean prev = students.hasPrevious();
            /*
            next = true
            prev = true
            - 결과: 버튼이 될 순 없음, 그냥 이전 페이지도 존재하고 이후 페이지도 존재한다는 사실만 알려주는 결과임

            페이징 처리 시에 버튼 알고리즘은 jpa에서 따로 제공하지 않기 때문에
            버튼 배치 알고리즘을 수행 할 클래스는 여전히 필요합니다.
            제공되는 정보는 이전보다 많기 때문에, 좀 더 수월하게 처리가 가능합니다.
            */

            //then
            System.out.println("\n\n\n");
            System.out.println("totalPages = " + totalPages);
            System.out.println("totalElements = " + totalElements);
            System.out.println("next = " + next);
            System.out.println("prev = " + prev);
            students.getContent().forEach(System.out::println);
            // 정렬은 주지 않았음!
            System.out.println("\n\n\n");

            /*

            - 목적: 이름에 3 들어갔으면 다 조회 해 오겠다!
            - 결과
            totalPages = 7
            totalElements = 66
            Student(id=402880858bebc71a018bebc724f80003, name=김테스트3, city=도시3, major=전공3)
            Student(id=402880858bebc71a018bebc724fa000d, name=김테스트13, city=도시13, major=전공13)
            Student(id=402880858bebc71a018bebc725000017, name=김테스트23, city=도시23, major=전공23)
            Student(id=402880858bebc71a018bebc72502001e, name=김테스트30, city=도시30, major=전공30)
            Student(id=402880858bebc71a018bebc72502001f, name=김테스트31, city=도시31, major=전공31)
            Student(id=402880858bebc71a018bebc725020020, name=김테스트32, city=도시32, major=전공32)
            Student(id=402880858bebc71a018bebc725020021, name=김테스트33, city=도시33, major=전공33)
            Student(id=402880858bebc71a018bebc725020022, name=김테스트34, city=도시34, major=전공34)
            Student(id=402880858bebc71a018bebc725030023, name=김테스트35, city=도시35, major=전공35)
            Student(id=402880858bebc71a018bebc725030024, name=김테스트36, city=도시36, major=전공36)
            */
        }
    }

