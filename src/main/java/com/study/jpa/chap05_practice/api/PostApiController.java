package com.study.jpa.chap05_practice.api;

import com.study.jpa.chap05_practice.dto.*;
import com.study.jpa.chap05_practice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "post API", description = "게시물 조회, 등록 및 수정, 삭제 api 입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET , param: (page, size)
        게시물 개별 조회: /posts/{id}       - GET
        - param으로 넘어오는 것은 쿼리스트링으로 값이 넘어오는 것
        {id}는 그냥 url에 묻어서 오는 것
        
        게시물 등록:     /posts            - POST , payload(: 유효 탑재량, 전송되는 순수한 데이터): writer, title, content, hashTags -> fetch로 프론트에서 보내는 이름.
        게시물 수정:     /posts            - PATCH, PUT, payload: {title, content, postNo}
        게시물 삭제:     /posts/{id}       - DELETE
     */
    private final PostService postService;


    // 게시물 목록 페이징 조회
    @GetMapping
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}", pageDTO.getPage(), pageDTO.getSize());

        // 화면단에게는 JSON형태로 전달이 될 것이다.
        PostListResponseDTO dto = postService.getPosts(pageDTO);

        return ResponseEntity.ok().body(dto);
        /*
        - ResponseEntity.ok()
        응답상태코드임. 200 상태를 리턴함
        */
    }


    // 특정 게시물 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){
        log.info("/api/v1/posts/{}", id);


        // 서비스에서 보낸 결과 값에 예외가 발생했다면 던지고 컨트롤러에서 받아 예외를 처리하겠다.
        try {
            PostDetailResponseDTO dto = postService.getDetail(id);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
            // 화면단에 잘못된 요청을 보냈다고 body에 예외의 매개 값인 e에 서비스에서 적었던 메세지를 전달한다.
        }

        /*
        - 결과 (id: 17)
        {
            "writer": "작성자17",
            "title": "제목17",
            "content": "내용17",
            "hashTags": [],
            "regDate": "2023/11/22"
        }
        - 결과 (id: 1777) - 400 Bad Request
        1777번 게시물이 존재하지 않습니다!
        */
    }

    // 게시글 등록 (post)
    @Operation(summary = "게시물 작성", description = "게시물 작성을 담당하는 메서드 입니다. 작성 후 등록 해 줍니다.")
    @Parameters({
            @Parameter(name = "writer", description = "게시물의 작성자 이름을 작성하세요.", example = "최파삭", required = true), // required = true -> 필수 값이라는 뜻
            @Parameter(name = "title", description = "게시물의 제목을 작성하세요.", example = "제목", required = true),
            @Parameter(name = "content", description = "게시물의 내용을 작성하세요.", example = "내용", required = false),
            @Parameter(name = "hashTags", description = "게시물의 해시 태그들을 작성하세요.", example = "['하하', '호호']")
            // 이렇게 작성 해 주면 지정한 api 홈페이지에 들어갔을 때 설명들이 나온다.
    })
    @PostMapping
    public ResponseEntity<?> create(
            @Validated @RequestBody PostCreateDTO dto,
            BindingResult result // 검증 에러 정보를 가진 객체
    ){

        log.info("/api/v1/posts POST!! - payload:{} ",dto);

        // 1단계
         if(dto == null){
             // DTO 객체가 전달 자체가 안된 경우에는 여기서 막아서 끝내겠다.
             return ResponseEntity.badRequest().body("등록 게시물 정보를 전달 해 주세요!");
         }
         // 2단계
        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        // 위의 if문들을 다 패스 했다면 = dto가 null도 아니고, 입력 값 검증도 모두 통과함. -> service에게 명령.
        try {
            PostDetailResponseDTO responseDTO = postService.insert(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 터짐뵤 먄. 원인 -> " + e.getMessage());
        }
    }
    @Operation(summary = "게시물 수정", description = "게시물 수정을 담당하는 메서드 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료!", content = @Content(schema = @Schema(implementation = PostDetailResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    

    // 게시물 수정
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> update(
            @Validated @RequestBody PostModifyDTO dto,
            BindingResult result,
            HttpServletRequest request // 여러 개의 요청 방식에 따라 다른 처리를 해줘야 한다.
    ){

        log.info("/api/v1/posts {} - payload: {}", request.getMethod(), dto);

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if(fieldErrors != null) return fieldErrors;

        PostDetailResponseDTO responseDTO = postService.modify(dto);

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        log.info("/api/v1/posts/{} DELETE!!", id);

        try {
            postService.delete(id);
            return ResponseEntity.ok("DEL SUCCESS!!");
        /*
        }
        - 방법 1
        catch (SQLIntegrityConstraintViolationException e){
            return ResponseEntity.internalServerError()
                    .body("해시태그가 달린 게시물은 삭제할 수 없습니다."); // 근본적인 해결 방법은 X
        */
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    // 입력값 검증(Validation)의 결과를 처리 해 주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if(result.hasErrors()){ // 입력 값 검증 단계에서 문제가 있었다면 -> true가 전달 된다.
            List<FieldError> fieldErrors = result.getFieldErrors();// -> error가 1개 일 경우 / 2개일 경우 -> getFieldErrors()
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString()); // 저 fieldErrors가 하나씩 전달 될 때마다 로그로 문자열로 출력 해 달라
            });
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

}
