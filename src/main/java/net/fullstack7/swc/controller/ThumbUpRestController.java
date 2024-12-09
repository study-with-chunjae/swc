package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.ThumbUp;
import net.fullstack7.swc.dto.ApiResponse;
import net.fullstack7.swc.repository.ThumbUpRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.PostServiceIf;
import net.fullstack7.swc.service.ThumbUpServiceIf;
import net.fullstack7.swc.util.CookieUtil;
import net.fullstack7.swc.util.LogUtil;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thumb-ups")
@Log4j2
@RequiredArgsConstructor
public class ThumbUpRestController {
    private final ThumbUpServiceIf thumbUpService;
    private final MemberServiceIf memberService;
    private final PostServiceIf postService;
    private final CookieUtil cookieUtil;

    @PostMapping("/{post-id}")
    public ResponseEntity<ApiResponse<?>> addThumbUp(@PathVariable(value = "post-id", required = false) Integer postId, HttpServletRequest request) {
        try{
            LogUtil.logLine("ThumbUpRestController -> addThumbUp");
            if(postId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 요청값입니다."));
            }
            String memberId = cookieUtil.getMemberIdInJwt(memberService, request);
            ThumbUp thumbUp = thumbUpService.addThumbUp(postId, memberId);
            if(thumbUp == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("오류 발생 다시시도하세요"));
            }
            return ResponseEntity.ok(ApiResponse.success("좋아요 성공"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
    @DeleteMapping("/{post-id}")
    public ResponseEntity<ApiResponse<?>> removeThumbUp(@PathVariable(value = "post-id", required = false) Integer postId, HttpServletRequest request) {
        try{
            LogUtil.logLine("ThumbUpRestController -> removeThumbUp");
            if(postId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 요청값입니다."));
            }
            String memberId = cookieUtil.getMemberIdInJwt(memberService, request);

            return thumbUpService.removeThumbUp(postId, memberId)
                    ? ResponseEntity.ok(ApiResponse.success("좋아요 취소 성공"))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("오류 발생 다시시도하세요."));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/{post-id}")
    public ResponseEntity<ApiResponse<?>> getThumbUpCount(@PathVariable(value = "post-id", required = false) Integer postId, HttpServletRequest request) {
        try{
            LogUtil.logLine("ThumbUpRestController -> getThumbUpCount");
            if(postId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 요청값입니다."));
            }
            Integer count = thumbUpService.getThumbUpCount(postId);
            if(count == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("좋아요 값 조회 실패"));
            }
            return ResponseEntity.ok(ApiResponse.success("좋아요 수 불러오기 성공",count));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}