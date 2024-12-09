package net.fullstack7.swc.handler;

import net.fullstack7.swc.domain.ChatMessage;
import net.fullstack7.swc.domain.ChatRoom;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.repository.ChatMessageRepository;
import net.fullstack7.swc.repository.ChatRoomReposotory;
import net.fullstack7.swc.repository.MemberRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
//    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomReposotory roomRepository;
    private final Map<String, WebSocketSession> activeSessions = new HashMap<>();
    private final MemberRepository memberRepository;

    public ChatWebSocketHandler(ChatMessageRepository messageRepository, ChatRoomReposotory roomRepository, MemberRepository memberRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            System.out.println("Received message: " + message.getPayload());

            JSONObject jsonMessage = new JSONObject(message.getPayload());
            String senderId = jsonMessage.getString("senderId");
            String receiverId  = jsonMessage.getString("receiverId");
            String textMessage = jsonMessage.getString("message");

            Member sender = memberRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender가 존재하지 않습니다."));
            Member receiver = memberRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver가 존재하지 않습니다."));
            System.out.println("Sender ID: " + senderId);
            System.out.println("Receiver ID: " + receiverId);


            // 메시지 저장
            ChatRoom chatRoom = roomRepository.findBySenderIdAndReceiverId(sender.getMemberId(), receiver.getMemberId());
            if (chatRoom == null) {
                // 채팅방이 존재하지 않으면 클라이언트로 오류 메시지 전송
//                String errorMessage = "{\"error\": \"채팅방이 존재하지 않습니다.\"}";
//                session.sendMessage(new TextMessage(errorMessage));
//                return;
                chatRoom = new ChatRoom();
                chatRoom.setSender(sender);
                chatRoom.setReceiver(receiver);
                chatRoom.setSenderStatus(1); // 상태 설정 (상태에 맞게 수정)
                chatRoom.setReceiverStatus(1); // 상태 설정 (상태에 맞게 수정)
                roomRepository.save(chatRoom); // 새로운 채팅방 저장
                System.out.println("새 채팅방이 생성되었습니다.");
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(textMessage);
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setIsRead(0); // 메시지는 기본적으로 읽지 않은 상태
            chatMessage.setChatRoom(chatRoom);
            chatMessage.setSender(new Member(senderId));

            messageRepository.save(chatMessage);

            // WebSocket을 통해 메시지 전송
            if (activeSessions.containsKey(chatRoom.getChatRoomId())) {
                WebSocketSession recipientSession = activeSessions.get(chatRoom.getChatRoomId());
                recipientSession.sendMessage(new TextMessage(message.getPayload())); // 채팅방에 메시지 전송
            } else {
                // 채팅방에 연결된 세션이 없을 경우 처리 (예: 세션이 종료된 경우)
                System.out.println("No active session for chat room " + receiverId );
                String errorMessage = "{\"error\": \"No active session for chat room " + chatRoom.getChatRoomId()  + "\"}";
                session.sendMessage(new TextMessage(errorMessage));
            }
        }catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();

            // 예외가 발생한 경우 클라이언트에게 오류 메시지 전송 (옵션)
            if (session.isOpen()) {
                String errorMessage = "{\"error\": \"Error processing message. Please try again.\"}";
                session.sendMessage(new TextMessage(errorMessage));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 설정된 후 처리할 로직
        System.out.println("WebSocket connection established: " + session.getId());
        activeSessions.putIfAbsent(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        activeSessions.remove(session.getId());
        System.out.println("WebSocket connection closed: " + session.getId());
    }
}

