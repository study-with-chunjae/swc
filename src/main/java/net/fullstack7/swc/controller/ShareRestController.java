package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Share;
import net.fullstack7.swc.dto.ApiResponse;
import net.fullstack7.swc.dto.FriendShareDTO;
import net.fullstack7.swc.dto.ShareDTO;
import net.fullstack7.swc.service.FriendServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.ShareServiceIf;
import net.fullstack7.swc.util.CheckJwtToken;
import net.fullstack7.swc.util.CookieUtil;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shares")
@Log4j2
@RequiredArgsConstructor
public class ShareRestController {
    private final ShareServiceIf shareService;
    private final FriendServiceIf friendService;
    private final MemberServiceIf memberService;
    private final CookieUtil cookieUtil;
    @CheckJwtToken
    @GetMapping("/{post-id}/not-shared-friends-list")
    public ResponseEntity<ApiResponse<?>> notSharedFriendsList(@PathVariable(value = "post-id", required = false) Integer postId, HttpServletRequest request){
        try{
            LogUtil.logLine("ShareRestController.notSharedFriendsList");
            if(postId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 입력값입니다."));
            }
            String memberId = cookieUtil.getMemberIdInJwt(memberService, request);
            List<FriendShareDTO> notSharedFriendsList = friendService.notSharedFriends(postId, memberId);
            List<Member> notSharedFriendList = new ArrayList<>();
            notSharedFriendsList.forEach(f->{
                if(!f.getReceiver().getMemberId().equals(memberId)){
                    notSharedFriendList.add(f.getReceiver());
                }else if(!f.getRequester().getMemberId().equals(memberId)){
                    notSharedFriendList.add(f.getRequester());
                }
            });
            return ResponseEntity.ok(ApiResponse.success("친구목록조회성공", notSharedFriendList));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
    @CheckJwtToken
    @PostMapping("/{post-id}/{shared-id}")
    public ResponseEntity<ApiResponse<?>> addShare(@PathVariable(value="post-id", required = false) Integer postId,
                                                   @PathVariable(value="shared-id", required = false)String sharedId,
                                                   HttpServletRequest request){
        try{
            if(postId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 입력입니다."));
            }
            if(sharedId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 입력입니다."));
            }
            Share share = shareService.addShare(
                    ShareDTO.builder()
                            .postId(postId)
                            .memberId(sharedId)
                            .build()
            );
            if(share == null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("일시적 오류입니다. 다시 시도하세요."));
            }
            return ResponseEntity.ok(ApiResponse.success("공유 완료"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
    @CheckJwtToken
    @DeleteMapping("/{post-id}/{shared-id}")
    public ResponseEntity<ApiResponse<?>> deleteShare(@PathVariable(value="post-id", required = false) Integer postId,
                                                      @PathVariable(value="shared-id", required = false) String sharedId,
                                                      HttpServletRequest request){
        try{
            if(postId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 입력입니다."));
            }
            if(sharedId == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("잘못된 입력입니다."));
            }
            boolean result = shareService.removeShare(
                    ShareDTO.builder()
                            .postId(postId)
                            .memberId(sharedId)
                            .build()
            );
            if(!result){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("일시적 오류입니다. 다시 시도하세요."));
            }
            return ResponseEntity.ok(ApiResponse.success("공유 해제 완료"));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}
