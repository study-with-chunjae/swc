package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Friend;
import net.fullstack7.swc.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    Optional<Friend> findByRequesterAndReceiver(Member requester, Member receiver);
    List<Friend> findByReceiverAndStatus(Member receiver, Integer status);
    List<Friend> findByRequesterOrReceiverAndStatus(Member requester, Member receiver, Integer status);

}
