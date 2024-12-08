package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.service.FriendServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendRestController {
    private final FriendServiceIf friendService;
    private final CookieUtil cookieUtil;
    private final MemberServiceIf memberService;

    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchFriends(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "5") int limit,
                                                      @RequestParam(defaultValue = "0") int page) {
        List<Member> results = friendService.searchFriends(keyword, limit, page);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendDTO friendDTO
                                                    ,HttpServletRequest request
    ) {
        String requesterId = getMemberIdInJwt(request);

        friendService.sendFriendRequest(requesterId, friendDTO);
        return ResponseEntity.ok("친구 요청이 전송되었습니다.");
    }

    @PostMapping("/accept/{friendId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Integer friendId
                                                      ,HttpServletRequest request
    ) {
        String receiverId = getMemberIdInJwt(request);
        friendService.acceptFriendRequest(friendId, receiverId);
        return ResponseEntity.ok("친구 요청이 수락되었습니다.");
    }

    @PostMapping("/reject/{friendId}")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Integer friendId
                                                      ,HttpServletRequest request
    ) {
        String receiverId = getMemberIdInJwt(request);
        friendService.rejectFriendRequest(friendId, receiverId);
        return ResponseEntity.ok("친구 요청이 거절되었습니다.");
    }

    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Integer friendId
                                               ,HttpServletRequest request
    ) {
        String memberId = getMemberIdInJwt(request);
        friendService.deleteFriend(friendId, memberId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendDTO>> getFriendRequests(HttpServletRequest request) {
        String receiverId = getMemberIdInJwt(request);
        List<FriendDTO> requests = friendService.getFriendRequests(receiverId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendDTO>> getFriends(HttpServletRequest request) {
        String memberId = getMemberIdInJwt(request);
        List<FriendDTO> friends = friendService.getFriends(memberId);
        return ResponseEntity.ok(friends);
    }

    private String getMemberIdInJwt(HttpServletRequest req){
        String accessToken = cookieUtil.getCookieValue(req,"accessToken");
        return memberService.getMemberInfo(accessToken).get("memberId");
    }
}
