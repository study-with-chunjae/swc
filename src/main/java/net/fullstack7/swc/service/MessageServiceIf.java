package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.dto.MessageDTO;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageServiceIf {
    // 쪽지 목록 조회(receiverId)
    List<Message> getReceiverMessageList(String memberId);

    // 쪽지 목록 조회(senderId)
    List<Message> getSenderMessageList(String memberId);

    // 메시지 인덱스로 조회
    Message getMessageById(Long messageId);

    // 쪽지 삭제
    void deleteMessages(List<Long> messageIds);

    // 쪽지 보내기
    Message sendMessage(String senderId, String receiverId, String content, String title, LocalDateTime regDate);

    // 쪽지 읽음 처리
    Message markAsRead(Long messageId);

    //개수
    public int getReceiverMessageCount(String memberId);

    public List<MessageDTO> getReceiverMessageList(String memberId, Pageable pageable);
}
