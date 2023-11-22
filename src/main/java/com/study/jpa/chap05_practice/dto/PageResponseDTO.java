package com.study.jpa.chap05_practice.dto;

import com.study.jpa.chap05_practice.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter @Setter
@ToString
public class PageResponseDTO {
    // 페이지 렌더링에 관한 정보들

    private int startPage;
    private int endPage;
    private int currentPage;
    private boolean prev;
    private boolean next;
    private int totalCount;

    // 한 페이지에 배치할 페이지 버튼 수 (1~10 // 11~20): 10개로 고정
    private static final int PAGE_COUNT = 10;
    
    public PageResponseDTO(Page<Post> pageData) {
        // 기존에 사용했던 PageCreator와 다를 것이 없다.
        // 매개 값(pageDate)으로 전달 된 객체가 많은 정보를 제공 하기 때문에 로직이 좀 더 간편해진 것 뿐 입니다.
        this.totalCount = (int) pageData.getTotalElements();
        this.currentPage = pageData.getPageable().getPageNumber() + 1;
        this.endPage
                = (int) (Math.ceil((double) currentPage / PAGE_COUNT) * PAGE_COUNT);
        this.startPage = endPage - PAGE_COUNT + 1;

        int realEnd = pageData.getTotalPages();

        // 마지막 페이지 보정
        if(realEnd < this.endPage) this.endPage = realEnd;

        // 버튼 활성화 보정
        this.prev = startPage > 1;
        this.next = endPage < realEnd;
    }
}
