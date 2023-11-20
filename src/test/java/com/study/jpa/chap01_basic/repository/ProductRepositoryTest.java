package com.study.jpa.chap01_basic.repository;

import com.study.jpa.chap01_basic.entity.Product;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.study.jpa.chap01_basic.entity.Product.Category.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트 완료 후 롤백. 원상 복수 시켜라! 라는 아노테이션
@Rollback(false) // 테스트 끝난 다음 롤백 시킨다(true) / 안시킨다(false)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;


    // 메서드 단위 별로 테스트 메서드 작성한다.
    @BeforeEach // 테스트 메서드 전에 실행되는 메서드
    // 이따가 삭제하거나 수정하거나 무엇을 하든 무조건 이 메서드가 실행이 되고 나서
    // 메서드들이 실행된다.
    // 순서: BeforEach메서드 -> 테스트 메서드
    void insertDummyData() {
        //given
        Product p1 = Product.builder()
                .name("아이폰")
                .category(ELECTRONIC)
                .price(1000000)
                .build();
        Product p2 = Product.builder()
                .name("탕수육")
                .category(FOOD)
                .price(20000)
                .build();
        Product p3 = Product.builder()
                .name("구두")
                .category(FASHION)
                .price(300000)
                .build();
        Product p4 = Product.builder()
                .name("쓰레기")
                .category(FOOD)
                .build();

        //when
        // 데이터 추가 메서드
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
        productRepository.save(p4);
        // save()를 통해서 INSERT문이 실행된다.
        // save()는 void 메서드가 아님
        // 리턴 타입: 엔터티 객체(Product임)

        //then
    }

    // insert
    @Test
    @DisplayName("5번째 상품을 DB에 저장해야 한다.")
    void testSave() {
        //given
        Product p5 = Product.builder()
                .name("정장")
                .category(FASHION)
                .price(1000000)
                .build();
        //when
        Product saved = productRepository.save(p5);

        //then
        assertNotNull(saved);
    }

    // delete
    @Test
    @DisplayName("id가 2번인 데이터를 삭제해야 한다")
    void  testRemove() {
        //given
        long id = 2L;
        //when
        productRepository.deleteById(id);
        //then
    }

    // select
    @Test
    @DisplayName("상품 전체 조회를 하면 상품의 개수가 4개여야 한다.")
    void testFindAll() {
        //given

        //when
        List<Product> products = productRepository.findAll();

        //then
        products.forEach(System.out::println);
        assertEquals(4, products.size());
    }

    @Test
    @DisplayName("3번 상품을 조회하면 상품명이 구두일 것이다.")
    void testFindOne() {
        //given
        long id = 3L;
        //when
        Optional<Product> product = productRepository.findById(id);
        // null 체크 해주는 클래스

        //then
        // 1번째 테스트
        product.ifPresent(p -> {
            assertEquals("구두", p.getName());
        });
        // 2번째 테스트
        Product foundProduct = product.get();
        assertNotNull(foundProduct);

        System.out.println("foundProduct = " + foundProduct);
    }

    // update
    @Test
    @DisplayName("2번 상품의 이름과 가격을 변경해야 한다")
    void testModify() {
        //given
        long id = 2L;
        String newName = "짜장면";
        int newPrice = 6000;
        //when
        // JPA는 update를 따로 메서드로 제공하지 않고,
        // 조회한 후 setter로 변경하면 자동으로 update문이 나갑니다.
        // 변경 후에는 save를 호출 해서 저장 해 줘야 한다.
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(p -> {
            p.setName(newName);
            p.setPrice(newPrice);

            productRepository.save(p);
        });

        //then
        assertTrue(product.isPresent());

        Product p = product.get();
        assertEquals("짜장면", p.getName());
    }
}