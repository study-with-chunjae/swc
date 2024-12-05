package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomReposotory extends JpaRepository<ChatRoom, Integer> {
}
