package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.ChatMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId")
    List<ChatMessage> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = :isRead WHERE cm.chatMessageId = :chatMessageId")
    void readByChatMessageId(@Param("isRead") Integer isRead, @Param("chatMessageId") String chatMessageId);
}
