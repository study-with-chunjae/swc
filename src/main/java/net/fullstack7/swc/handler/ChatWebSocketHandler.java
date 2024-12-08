package net.fullstack7.swc.handler;

import jakarta.servlet.http.HttpServletRequest;
import net.fullstack7.swc.config.JwtTokenProvider;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.net.URI;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public ChatWebSocketHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // WebSocket 연결이 성립되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String token = null;
//        session.sendMessage(new TextMessage("Please send token for authentication"));

        // URL의 쿼리 파라미터에서 JWT 토큰을 추출
        if (uri != null && uri.getQuery() != null) {
            String[] params = uri.getQuery().split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    token = param.substring(6);
                    break;
                }
            }
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효한 경우 memberId를 WebSocket 세션에 추가
            String memberId = jwtTokenProvider.getMemberId(token);
            session.getAttributes().put("memberId", memberId);
            System.out.println("memberId " + memberId + "채팅연결");
        } else {
            System.out.println("유효하지 않은 토큰");
            session.sendMessage(new TextMessage("로그인하세요."));
            session.close();
        }
    }

    // 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String memberId = (String) session.getAttributes().get("memberId");
        if (memberId != null) {
            System.out.println("User " + memberId + "message: " + message.getPayload());
        } else {
            session.sendMessage(new TextMessage("메시지를 전송할 수 없습니다."));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String memberId = (String) session.getAttributes().get("memberId");
        if (memberId != null) {
            System.out.println("User " + memberId + "채팅끝남");
        }
    }
}
