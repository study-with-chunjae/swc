package net.fullstack7.swc.handler;

import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.domain.ChatMessage;
import net.fullstack7.swc.domain.ChatRoom;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.ChatMessageDTO;
import net.fullstack7.swc.repository.ChatMessageRepository;
import net.fullstack7.swc.repository.ChatRoomReposotory;
import net.fullstack7.swc.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomReposotory chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // WebSocketSession에서 JWT 토큰을 추출하여 userId를 반환하는 메서드
    private String getUserIdFromSession(WebSocketSession session) {
        String token = session.getHandshakeHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 부분 제거
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getMemberId(token);
            }
        }
        throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        sessions.put(userId, session);
        session.sendMessage(new TextMessage("채팅방 연결"));
    }

    // 메시지를 받을 때
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userId = getUserIdFromSession(session);
        String payload = message.getPayload();

        // 메시지를 보낼 대상(상대방)을 찾아 메시지 전송
        String[] messageParts = payload.split(":", 2);
        String receiverId = messageParts[0];
        String textMessage = messageParts[1];

        // ChatRoom을 찾기 위해 sender와 receiver의 Member 객체를 사용하여 조회
        Optional<Member> senderOpt = memberRepository.findById(userId);
        Optional<Member> receiverOpt = memberRepository.findById(receiverId);

        if (!senderOpt.isPresent() || !receiverOpt.isPresent()) {
            session.sendMessage(new TextMessage("유효하지 않은 아이디입니다."));
            return;
        }

        Member sender = senderOpt.get();
        Member receiver = receiverOpt.get();

        // 채팅방을 찾음 (sender와 receiver 기준으로 찾음)
        ChatRoom chatRoom = chatRoomRepository.findBySenderIdAndReceiverId(userId, receiverId);
        if (chatRoom == null) {
            chatRoom = chatRoomRepository.findBySenderIdAndReceiverId(receiverId, userId);
        }

        if (chatRoom == null) {
            chatRoom = new ChatRoom(sender, receiver, 1, 1);
            chatRoomRepository.save(chatRoom);
        }

        // 채팅 메시지 저장
        ChatMessage chatMessage = new ChatMessage(chatRoom, sender, textMessage, LocalDateTime.now(), 0);
        chatMessageRepository.save(chatMessage);

        // 상대방에게 메시지 전송
        WebSocketSession receiverSession = sessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                // 메시지 전송
                ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
                chatMessageDTO.setChatRoomId(chatRoom.getChatRoomId());
                chatMessageDTO.setSenderId(sender.getMemberId());
                chatMessageDTO.setMessage(textMessage);
                chatMessageDTO.setCreatedAt(LocalDateTime.now());
                chatMessageDTO.setIsRead(0);

                // 상대방에게 메시지 전송
                receiverSession.sendMessage(new TextMessage("New message: " + textMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 연결 종료 시 세션에서 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        sessions.remove(userId);
    }
}
