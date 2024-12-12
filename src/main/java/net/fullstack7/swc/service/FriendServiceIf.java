package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FriendServiceIf {
    void sendFriendRequest(String requesterId, FriendDTO friendDTO);
    void acceptFriendRequest(Integer friendId, String receiverId);
    void rejectFriendRequest(Integer friendId, String receiverId);
    void deleteFriend(Integer friendId, String memberId);
    List<FriendDTO> getFriendRequests(String receiverId);
    List<FriendDTO> getFriends(String memberId);
    List<MemberDTO> searchFriends(String keyword, int limit, int page);

    //강감찬추가
    PageDTO<FriendListDTO> getFriendList(PageDTO<FriendListDTO> pageDTO, String memberId);
    int getTotalCount(PageDTO<FriendListDTO> pageDTO, String memberId);
    PageDTO<FriendListDTO> getFriendRequestList(PageDTO<FriendListDTO> pageDTO, String memberId);
    int getRequestTotalCount(PageDTO<FriendListDTO> pageDTO, String memberId);
    List<FriendShareDTO> notSharedFriends(Integer postId, String memberId);
    //강감찬추가
}
