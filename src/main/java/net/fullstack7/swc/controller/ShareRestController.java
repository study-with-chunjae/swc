package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.ApiResponse;
import net.fullstack7.swc.dto.FriendShareDTO;
import net.fullstack7.swc.service.FriendServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.ShareServiceIf;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/not-shared-friends-list/{post-id}")
    public ResponseEntity<ApiResponse<?>> notSharedFriendsList(@PathVariable(value = "post-id", required = false) Integer postId, HttpServletRequest request){
        try{
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

}
