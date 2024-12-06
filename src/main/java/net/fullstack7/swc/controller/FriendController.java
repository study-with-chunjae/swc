package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.service.FriendServiceIf;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
public class FriendController {
    private final FriendServiceIf friendService;

    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchFriends(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "5") int limit) {
        List<Member> results = friendService.searchFriends(keyword, limit);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendDTO friendDTO
                                                    //,Authentication authentication
    ) {
        //String requesterId = authentication.getName();
        String requesterId = "user1";

        friendService.sendFriendRequest(requesterId, friendDTO);
        return ResponseEntity.ok("친구 요청이 전송되었습니다.");
    }

    @PostMapping("/accept/{friendId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Integer friendId
    //                                                  ,Authentication authentication
    ) {
        //String receiverId = authentication.getName();
        String receiverId = "user1";
        friendService.acceptFriendRequest(friendId, receiverId);
        return ResponseEntity.ok("친구 요청이 수락되었습니다.");
    }

    @PostMapping("/reject/{friendId}")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Integer friendId
    //                                                  ,Authentication authentication
    ) {
        //String receiverId = authentication.getName();
        String receiverId = "user1";
        friendService.rejectFriendRequest(friendId, receiverId);
        return ResponseEntity.ok("친구 요청이 거절되었습니다.");
    }

    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Integer friendId
    //                                           ,Authentication authentication
    ) {
//        String memberId = authentication.getName();
        String memberId = "user1";
        friendService.deleteFriend(friendId, memberId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendDTO>> getFriendRequests(
           // Authentication authentication
    ) {
//        String receiverId = authentication.getName();
        String receiverId = "user1";
        List<FriendDTO> requests = friendService.getFriendRequests(receiverId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendDTO>> getFriends(
            Authentication authentication
    ) {
//        String memberId = authentication.getName();
        String memberId = "user1";
        List<FriendDTO> friends = friendService.getFriends(memberId);
        return ResponseEntity.ok(friends);
    }



}
