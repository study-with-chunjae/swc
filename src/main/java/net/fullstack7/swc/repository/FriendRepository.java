package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
}
