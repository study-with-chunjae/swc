package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AlertServiceImpl alertService;
    private final MemberRepository memberRepository;


    // 쪽지 목록 조회
    public List<Message> getMessageList(String receiverId) {
        // 수신자 ID를 통해 쪽지 목록을 조회
        return messageRepository.findByReceiverId(receiverId);
    }

    // 특정 메시지 조회
    public Message getMessageById(Long messageId) {
        // 메시지 ID로 해당 메시지 조회
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }

    // 쪽지 삭제
    public void deleteMessages(List<Long> messageIds) {
        messageRepository.deleteAllById(messageIds);  // 해당 조건에 맞는 모든 메시지 삭제
       }


    // 쪽지 보내기
    public Message sendMessage(String senderId, String receiverId, String content) {
        // 발신자와 수신자가 유효한지 확인
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // 메시지 객체 생성
        Message message = new Message(senderId, receiverId, content, false);
        return messageRepository.save(message);  // 메시지를 DB에 저장
    }

    // 쪽지 읽음 처리
    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // 읽음 처리
        message.setRead(true);
        return messageRepository.save(message);  // 업데이트된 메시지 저장
    }

}

