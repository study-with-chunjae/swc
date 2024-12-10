package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverId(String receiverId);

    List<Message> findBySenderId(String senderId);

    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

//    void deleteAllById(List<Long> messageIds);
}
