package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Friend;
import net.fullstack7.swc.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    Optional<Friend> findByRequesterAndReceiver(Member requester, Member receiver);
    List<Friend> findByReceiverAndStatus(Member receiver, Integer status);
    // 1
    List<Friend> findByRequesterOrReceiverAndStatus(Member requester, Member receiver, Integer status);
    // 0
    List<Friend> findByRequesterAndStatus(Member requester, Integer status);
    List<Friend> findByRequesterOrReceiver(Member requester, Member receiver);

    // 친구 신청리스트 페이징 ( 추후 확인 )
    Page<Friend> findByReceiverAndStatusOrderByRegDateDesc(Member receiver, Integer status, Pageable pageable);

    // 친구 목록 페이징 ( 추후 확인 )
    Page<Friend> findByRequesterOrReceiverOrderByRegDateDesc(Member requester, Member receiver, Pageable pageable);
}
