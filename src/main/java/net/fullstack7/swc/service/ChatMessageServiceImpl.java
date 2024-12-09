package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.ChatMessage;
import net.fullstack7.swc.domain.ChatRoom;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.repository.ChatMessageRepository;
import net.fullstack7.swc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageServiceIf{
    private final MemberRepository memberRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getMessages(Long chatRoomId, String senderId){
        return chatMessageRepository.findByChatRoomId(chatRoomId);
    }

    public ChatMessageServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void sendMessage(ChatMessage message, ChatRoom room) {
        String receiverId = room.getReceiver().getMemberId();
        if (receiverId == null || !isReceiverValid(receiverId)) {
//            throw new RuntimeException("Receiver is not valid");
            receiverId = getDefaultReceiverId();
        }
//        if(room.getReceiver() == null || !isReceiverValid(room.getReceiver().getMemberId())){
//            throw new RuntimeException("Receiver is not valid");
//        }
    }

    private boolean isReceiverValid(String receiverId) {
        Member receiver = memberRepository.findByMemberId(receiverId);
//        if (receiver == null || receiverId.isEmpty()) {
//            return false;
//        }
//        return memberRepository.existsById(receiver);
//        return true;
          return receiver != null;
    }

    private String getDefaultReceiverId() {
        // 기본 receiverId를 설정하는 로직
        // 예를 들어, 가장 첫 번째 유효한 사용자를 찾을 수 있습니다.
        Member defaultReceiver = memberRepository.findAll().stream().findFirst().orElse(null);
        return defaultReceiver != null ? defaultReceiver.getMemberId() : "defaultReceiverId";
    }

}
