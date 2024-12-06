package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.ChatRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomReposotory extends JpaRepository<ChatRoom, Integer> {
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sender.memberId = :senderId")
    List<ChatRoom> findBySenderId(@Param("senderId") String senderId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.receiver.memberId = :receiverId")
    List<ChatRoom> findByReceiverId(@Param("receiverId") String receiverId);

    //개별상태변경할때
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.receiverStatus = :receiverStatus WHERE cr.receiver.memberId = :receiverId ")
    void deleteByReceiverId(@Param("receiverId") String receiverId, @Param("receiverStatus") String receiverStatus);

    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.senderStatus = :senderStatus WHERE cr.sender.memberId = :senderId ")
    void deleteBySenderId(@Param("senderId") String senderId, @Param("receiverStatus") String senderStatus);

    //같이 변경할때(얘는 상황보고 독립적으로 컨트롤 가능하면 지우고 안되면 이거 사용하기)
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.senderStatus = :senderStatus, cr.receiverStatus = :receiverStatus WHERE cr.sender.memberId = :senderId AND cr.receiver.memberId = :receiverId")
    void deleteBySenderIdAndReceiverId(@Param("senderId") String senderId,
                                      @Param("receiverId") String receiverId,
                                      @Param("senderStatus") String senderStatus,
                                      @Param("receiverStatus") String receiverStatus);

}
