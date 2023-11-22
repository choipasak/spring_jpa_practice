package com.study.jpa.chap05_practice.dto;

import com.study.jpa.chap05_practice.entity.Post;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateDTO {

    /*
     @NotNull -> null 허용X. "", " "은 허용 함
     @NotEmpty -> null, ""을 허용X. 그대신 " "은 허용
     @NotBlank -> null, "", " " 허용X

     위의 아노테이션을 달아주고 이 DTO를 받는다고 한 부분에 @Validated 아노테이션도 같이
     달아줘야 사용할 수 있다. (컨트롤러의 create())
    */
    
    @NotBlank
    @Size(min = 2, max = 5)
    // 필드명 똑같이(payload랑) 맞춰 줘야 함
    private String writer;
    
    @NotBlank
    @Size(min = 1, max = 20)
    private String title;
    private String content;
    private List<String> hashTags;
    
    // DTO -> Entity 해주는 메서드(리턴값이 필요하므로 생성자로 만들면 안됌) : 재활용성
    // 왜: jpa를 구현하는 레파지토리가 엔터티를 받는다고 작성해줬기 때문에
    // toEntity() -> 관례적 메서드명
    public Post toEntity(){
        
        return Post.builder()
                .writer(this.writer)
                .content(this.content)
                .title(this.title)
                //.hashTags(this.hashTags)
                // 해시태그는 여기서 insert하는 것이 아님. hashTags는 조회를 해 오는 읽기 전용 컬럼이다.
                .build();
        
    }

}
