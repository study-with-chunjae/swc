package net.fullstack7.swc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.fullstack7.swc.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(jwtTokenProvider), "/chat")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        // Authorization 헤더에서 토큰을 추출
                        String token = request.getHeaders().getFirst("Authorization");
                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7); // "Bearer "를 제거하고 토큰만 추출

                            if (jwtTokenProvider.validateToken(token)) {
                                String memberId = jwtTokenProvider.getMemberId(token);
                                attributes.put("memberId", memberId);  // WebSocket 세션에 memberId 저장
                                return true; // 토큰 유효하면 연결 허용
                            }
                        }

                        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                        return false; // 토큰이 없거나 유효하지 않으면 연결 거부
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                        // 후처리 필요 없으면 비워둘 수 있음
                    }
                });
    }
}
