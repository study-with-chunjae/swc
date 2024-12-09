package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.dto.FriendShareDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FriendServiceIf {
    void sendFriendRequest(String requesterId, FriendDTO friendDTO);
    void acceptFriendRequest(Integer friendId, String receiverId);
    void rejectFriendRequest(Integer friendId, String receiverId);
    void deleteFriend(Integer friendId, String memberId);
    List<FriendDTO> getFriendRequests(String receiverId);
    List<FriendDTO> getFriends(String memberId);
    List<Member> searchFriends(String keyword, int limit, int page);

    //강감찬추가
    List<FriendShareDTO> notSharedFriends(Integer postId, String memberId);
    //강감찬추가
}
