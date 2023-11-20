package com.study.jpa.chap02_querymethod.repository;

import com.study.jpa.chap02_querymethod.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void insertData(){
        Student s1 = Student.builder()
                .name("춘식이")
                .city("서울시")
                .major("수학과")
                .build();
        Student s2 = Student.builder()
                .name("언년이")
                .city("부산시")
                .major("수학교육과")
                .build();
        Student s3 = Student.builder()
                .name("대길이")
                .city("한양 도성")
                .major("체육과")
                .build();

        studentRepository.save(s1);
        studentRepository.save(s2);
        studentRepository.save(s3);
    }
    
    // 쿼리 메서드로 테스트 진행
    @Test
    @DisplayName("이름이 춘식이인 학생의 정보를 조회해야 한다.")
    void testFindByName() {
        //given
        String name = "춘식이";
        //when
        // jpa가 제공하지 않는 메서드는 직접 작성해서 사용 해 줘야 하는데
        // 직접 만드는 방법이 쿼리 메서드이다!
        List<Student> students = studentRepository.findByName(name);

        //then
        assertEquals(1, students.size());
        System.out.println("students = " + students.get(0));

        // 결과
        // : students = Student(id=402880858beb42ba018beb42c4e70000, name=춘식이, city=서울시, major=수학과)
    }

    @Test
    @DisplayName("testFindByCityAndMajor")
    void testFindByCityAndMajor() {
        //given
        String city = "부산시";
        String major = "수학교육과";
        //when
        List<Student> students = studentRepository.findByCityAndMajor(city, major);

        //then
        assertEquals(1, students.size());
        assertEquals("언년이", students.get(0).getName());

        System.out.println("students.get(0) = " + students.get(0));
    }
    
    @Test
    @DisplayName("findByMajorContaining")
    void findByMajorContaining() {
        //given
        String major = "수학";
        //when
        List<Student> students = studentRepository.findByMajorContaining(major);

        //then
        assertEquals(2, students.size());

        System.out.println("\n\n\n");
        students.forEach(System.out::println);
        System.out.println("\n\n\n");
    }
    
    @Test
    @DisplayName("testNativeSQL")
    void testNativeSQL() {
        //given
        String name = "대길이";
        //when
        Student student = studentRepository.findNameWithSQL(name);
        //then
        assertEquals("한양 도성", student.getCity());
        
        System.out.println("\n\n\n");
        System.out.println("student = " + student);
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("testFindCityWithJPQL")
    void testFindCityWithJPQL() {
        //given
        String city = "서울시";
        //when
        List<Student> list = studentRepository.getByCityWithJPQL(city);

        //then
        assertEquals("춘식이", list.get(0).getName());
        System.out.println("list = " + list);

        // 결과
        // list = [Student(id=402880858beb827d018beb8287660000, name=춘식이, city=서울시, major=수학과)]

    }

    @Test
    @DisplayName("testSearchNameWithJPQL")
    void testSearchNameWithJPQL() {
        //given
        String name = "이";
        //when
        List<Student> list = studentRepository.searchByNameWithJPQL(name);

        //then
        assertEquals(3, list.size());
        System.out.println("\n\n\n");
        list.forEach(System.out::println);
        System.out.println("\n\n\n");

        // 결과
        //  Student(id=402880858beb899d018beb89a89f0000, name=춘식이, city=서울시, major=수학과)
        //  Student(id=402880858beb899d018beb89a8ce0001, name=언년이, city=부산시, major=수학교육과)
        //  Student(id=402880858beb899d018beb89a8cf0002, name=대길이, city=한양 도성, major=체육과)

    }

    @Test
    @DisplayName("JPQL로 삭제하기")
    void testDeleteByJPQL() {
        //given
        String name = "대길이";
        //when
        studentRepository.deleteByNameWithJQPL(name); // 대길이 삭제
        //then
        List<Student> students = studentRepository.findByName(name); // 대길이 삭제 후 찾기

        // 대길이를 지우고 다시 대길이를 찾아달라고 했으니 list의 size가 0이 되었을 것이다.
        assertEquals(0, students.size());

    }





}