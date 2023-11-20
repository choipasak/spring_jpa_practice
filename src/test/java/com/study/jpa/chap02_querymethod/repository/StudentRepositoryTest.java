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





}