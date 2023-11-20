package com.study.jpa.chap02_querymethod.repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    // 메서드 명으로는 필드명을 작성 해 줘야 한다
    // 이름 값 받아서 학생 정보 리턴 해 주겠다.
    List<Student> findByName(String name);

    List<Student> findByCityAndMajor(String city, String major);

    List<Student> findByMajorContaining(String major);

    // 네이티브 쿼리 사용
    @Query(value = "SELECT * FROM tbl_student WHERE stu_name = :nm", nativeQuery = true)
    Student findNameWithSQL(@Param("nm") String name);
    // 매개 값으로 들어온 name 이 SQL문의 ?에 들어가야 한다.

    // JPQL
    // SELECT 별칭 FROM 엔터티클래스명 AS 별칭
    // WHERE 별칭.필드명 = ?
    // SELECT st FROM Student AS st
    // WHERE st.name = ?

    // native-sql
    // SELECT 컬럼명 FROM 테이블명
    // WHERE 컬럼 = ?

    // 결론: SPQL은 JPA에게 명령을 내리는 것이고, native는 그냥 SQL에게 내리는 명령이다.
    // SPQL -> 만든 Entity클래스 필드명과 클래스를 사용해 객체를 포장해서 명령을 내리기만 하면 된다!

    // 도시 이름으로 학생 조회
//    @Query(value = "SELECT * FROM tbl_student WHERE city = ?", nativeQuery = true)

    // 사용자 정의 메서드
    @Query("SELECT s FROM Student AS s WHERE s.city = ?1")
    List<Student> getByCityWithJPQL(String city);


    @Query("SELECT s FROM Student AS s WHERE s.name LIKE %:nm%")
    List<Student> searchByNameWithJPQL(@Param("nm") String name);

    // JPQL로 수정 삭제 쿼리 쓰기
    @Modifying // JPQL을 통해서 DB 연동작업을 진행할 때 SELECT가 아니라면 무조건 붙어야 하는 아노테이션
    @Query("DELETE FROM Student s WHERE s.name LIKE %:nm%")
    void deleteByNameWithJQPL(@Param("nm") String name);





}
