package net.fullstack7.swc.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.ApiResponse;
import net.fullstack7.swc.dto.PostMainDTO;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.PostServiceIf;
import net.fullstack7.swc.util.CheckJwtToken;
import net.fullstack7.swc.util.CookieUtil;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Log4j2
public class PostRestController {
    private final PostServiceIf postService;
    private final CookieUtil cookieUtil;
    private final MemberServiceIf memberService;
    private static final Integer TYPE_SHARE = 1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String NOW_STRING = FORMATTER.format(LocalDate.now());
    @DeleteMapping("/my-posts/{postId}")
    @CheckJwtToken
    public ResponseEntity<ApiResponse<?>> deletePost(@PathVariable Integer postId, HttpServletRequest request) {
        try {
            String memberId = cookieUtil.getMemberIdInJwt(memberService, request);
            boolean result = postService.deletePost(postId, memberId);
            if(!result) {
                return ResponseEntity.internalServerError().body(ApiResponse.error("게시글 삭제 실패"));
            }
            return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공"));
        }catch(Exception e) {
            LogUtil.log(e.toString(),e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error(e.getMessage()));
        }
    }

    @CheckJwtToken
    @GetMapping("/main-posts/{member-id}/{created-at}")
    public ResponseEntity<ApiResponse<?>> getMainPost(@PathVariable(value = "created-at", required = false) String createdAt,
                                                      @PathVariable(value="member-id", required = false) String memberId,
                                                      HttpServletRequest request) {
        try{
            if(createdAt == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 입력값"));
            }
            List<PostMainDTO> postMainDTOList = postService.mainPost(LocalDate.parse(createdAt,FORMATTER).atStartOfDay(),memberId,TYPE_SHARE);
            if(postMainDTOList==null || postMainDTOList.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("조회된 내용이 없습니다.", postMainDTOList));
            }
            LogUtil.log("postList",postMainDTOList);
            LogUtil.log("response",ApiResponse.success("조회성공", postMainDTOList));
            return ResponseEntity.ok(ApiResponse.success("조회성공", postMainDTOList));
        }catch(Exception e) {
            LogUtil.log(e.toString(),e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error(e.getMessage()));
        }
    }
}
