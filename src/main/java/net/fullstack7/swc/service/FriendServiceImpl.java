package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Friend;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.repository.FriendRepository;
import net.fullstack7.swc.repository.MemberRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendServiceIf {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    // private final AlertService alertService; // 알림 기능 통합 시 사용

    @Override
    @Transactional
    public void sendFriendRequest(String requesterId, FriendDTO friendDTO) {
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("요청자 회원이 존재하지 않습니다."));
        Member receiver = memberRepository.findById(friendDTO.getReceiver())
                .orElseThrow(() -> new RuntimeException("수신자 회원이 존재하지 않습니다."));

        // 이미 친구 관계인지 확인
        Optional<Friend> existingFriend = friendRepository.findByRequesterAndReceiver(requester, receiver);
        if (existingFriend.isPresent()) {
            throw new RuntimeException("이미 친구 요청을 보냈거나 친구 관계입니다.");
        }

        // 반대 방향의 친구 요청도 확인 (상호 친구 관계)
        existingFriend = friendRepository.findByRequesterAndReceiver(receiver, requester);
        if (existingFriend.isPresent()) {
            throw new RuntimeException("이미 친구 요청을 보냈거나 친구 관계입니다.");
        }

        // 친구 요청 생성 (상태: 0 - 대기)
        Friend friend = new Friend(receiver, requester, 0);
        friendRepository.save(friend);

        // 알림 생성 (알림 기능 통합 시 사용)
        // String message = requester.getName() + "님이 친구 요청을 보냈습니다.";
        // alertService.createAlert(receiver, AlertType.FRIEND_REQUEST, message);
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Integer friendId, String receiverId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("친구 요청이 존재하지 않습니다."));
        if (!friend.getReceiver().getMemberId().equals(receiverId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        if (friend.getStatus() != 0) { // 상태가 대기인지 확인
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        // 친구 요청 수락 (상태: 1 - 수락)
        friend.allow();
        friendRepository.save(friend);

        // 알림 생성 (알림 기능 통합 시 사용)
        // String message = receiverId + "님이 친구 요청을 수락했습니다.";
        // alertService.createAlert(friend.getRequester(), AlertType.FRIEND_ACCEPTED, message);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(Integer friendId, String receiverId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("친구 요청이 존재하지 않습니다."));
        if (!friend.getReceiver().getMemberId().equals(receiverId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        if (friend.getStatus() != 0) { // 상태가 대기인지 확인
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        // 친구 요청 거절은 친구 관계를 삭제하는 것과 동일
        friendRepository.delete(friend);

        // 알림 생성 (알림 기능 통합 시 사용)
        // String message = receiverId + "님이 친구 요청을 거절했습니다.";
        // alertService.createAlert(friend.getRequester(), AlertType.FRIEND_REJECTED, message);
    }

    @Override
    @Transactional
    public void deleteFriend(Integer friendId, String memberId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("친구 관계가 존재하지 않습니다."));
        if (!friend.getRequester().getMemberId().equals(memberId) &&
                !friend.getReceiver().getMemberId().equals(memberId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 친구 관계 삭제
        friendRepository.delete(friend);

        // 상대방의 ID 파악 (필요 시 알림을 위해)
        String otherMemberId = friend.getRequester().getMemberId().equals(memberId)
                ? friend.getReceiver().getMemberId()
                : friend.getRequester().getMemberId();

        // 알림 생성 (알림 기능 통합 시 사용)
        // String message = "당신과 " + otherMemberId + "님이 친구를 삭제했습니다.";
        // alertService.createAlert(friend.getRequester(), AlertType.FRIEND_DELETED, message);
        // alertService.createAlert(friend.getReceiver(), AlertType.FRIEND_DELETED, message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendDTO> getFriendRequests(String receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        List<Friend> pendingFriends = friendRepository.findByReceiverAndStatus(receiver, 0);
        return pendingFriends.stream().map(friend -> {
            FriendDTO dto = new FriendDTO();
            dto.setFriendId(friend.getFriendId());
            dto.setRequester(friend.getRequester().getMemberId());
            dto.setReceiver(friend.getReceiver().getMemberId());
            dto.setStatus(friend.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendDTO> getFriends(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        List<Friend> friends = friendRepository.findByRequesterOrReceiverAndStatus(
                member, member, 1);
        return friends.stream().map(friend -> {
            FriendDTO dto = new FriendDTO();
            dto.setFriendId(friend.getFriendId());
            dto.setRequester(friend.getRequester().getMemberId());
            dto.setReceiver(friend.getReceiver().getMemberId());
            dto.setStatus(friend.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> searchFriends(String keyword, int limit, int page) {
        Pageable pageable = PageRequest.of(page, limit);
        return memberRepository.findById(keyword, pageable);
    }


}
