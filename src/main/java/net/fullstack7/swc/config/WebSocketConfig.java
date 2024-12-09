package net.fullstack7.swc.config;

import net.fullstack7.swc.handler.ChatWebSocketHandler;
import net.fullstack7.swc.repository.ChatMessageRepository;
import net.fullstack7.swc.repository.ChatRoomReposotory;
import net.fullstack7.swc.repository.ChatRoomReposotory;
import net.fullstack7.swc.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomReposotory roomRepository;
    private final MemberRepository memberRepository;

    // 생성자 주입
    public WebSocketConfig(ChatMessageRepository messageRepository, ChatRoomReposotory roomRepository, MemberRepository memberRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 의존성을 주입받아 ChatWebSocketHandler 생성
        ChatWebSocketHandler chatWebSocketHandler = new ChatWebSocketHandler(messageRepository, roomRepository, memberRepository);
        registry.addHandler(chatWebSocketHandler, "/chat")
                .setAllowedOriginPatterns("*");
    }
}
