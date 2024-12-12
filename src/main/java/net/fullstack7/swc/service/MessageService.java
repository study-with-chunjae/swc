package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.dto.MessageDTO;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements MessageServiceIf {

    private final MessageRepository messageRepository;
    private final AlertServiceImpl alertService;
    private final MemberRepository memberRepository;

    // 쪽지 보내기
//    public void sendMessage(String senderId, String receiverId, String content){
//        boolean receiverExists = memberRepository.existsById(receiverId);
//        if(!receiverExists){
//            throw new IllegalArgumentException("사용자가 존재하지 않음.");
//        }
//
//        Message message = new Message();
//        message.setSenderId(senderId);
//        message.setReceiverId(receiverId);
//        message.setContent(content);
//        messageRepository.save(message);
//    }


    // 쪽지 목록 조회(receiverId)
    @Override
    public List<Message> getReceiverMessageList(String memberId) {
        return messageRepository.findByReceiverId(memberId);
    }
    // 쪽지 목록 조회(senderId)
    @Override
    public List<Message> getSenderMessageList(String memberId) {
        return messageRepository.findBySenderId(memberId);
    }




    // 메시지 인덱스로 조회
    @Override
    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }

    // 쪽지 삭제
    @Override
    public void deleteMessages(List<Long> messageIds) {
        messageRepository.deleteAllById(messageIds);
       }


    // 쪽지 보내기
    @Override
    public Message sendMessage(String senderId, String receiverId, String content, String title, LocalDateTime regDate) {
//        Member sender = memberRepository.findById(senderId)
//                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
//        Member receiver = memberRepository.findById(receiverId)
//                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .title(title)
                .regDate(regDate)
                .isRead(false)
                .build();

//        Message message = new Message(senderId, receiverId, content, false);
        return messageRepository.save(message);
    }

    // 쪽지 읽음 처리
    @Override
    public Message markAsRead(Long messageId) {
        Message message = getMessageById(messageId);
        if (!message.isRead()) {
            message.setRead(true);
            messageRepository.save(message);
        }
        return message;
    }

    // 수신 메시지 개수 조회
    public int getReceiverMessageCount(String memberId) {
        return messageRepository.countByReceiverId(memberId); // 메시지 수를 카운트하는 메서드 호출
    }

    @Override
    public int getSenderMessageCount(String memberId) {
        return messageRepository.countBySenderId(memberId);
    }

    @Override
    public List<MessageDTO> getReceiverMessageList(String memberId, java.awt.print.Pageable pageable) {
        return List.of();
    }

    // 수신 메시지 목록 페이징 조회
    public List<MessageDTO> getReceiverMessageList(String memberId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findByReceiverId(memberId, pageable);
        return messagePage.map(this::convertToDTO).getContent(); // Message를 MessageDTO로 변환
    }

    // 발신 메시지 목록 페이징 조회
    public List<MessageDTO> getSenderMessageList(String memberId, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findBySenderId(memberId, pageable);
        return messagePage.map(this::convertToDTO).getContent(); // Message를 MessageDTO로 변환
    }

    // Message를 MessageDTO로 변환하는 메서드
    private MessageDTO convertToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .title(message.getTitle())
                .regDate(message.getRegDate())
                .isRead(message.isRead())
                .build();
    }

}

