package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverId(String receiverId);

    List<Message> findBySenderId(String senderId);

    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

    //받은메시지 목록 페이징
    Page<Message> findByReceiverId(String receiverId, Pageable pageable);
    //보낸메시지 목록 페이징
    Page<Message> findBySenderId(String senderId, Pageable pageable);


//    void deleteAllById(List<Long> messageIds);

    // 아이디 전체 삭제 (한덕용 추가)
    void deleteAllBySenderIdOrReceiverId(String senderId, String receiverId);
}
