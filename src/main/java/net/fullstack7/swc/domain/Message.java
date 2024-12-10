package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;  // 메시지 ID

        @Column(nullable = false)
        private String senderId;  // 발신자 ID

        @Column(nullable = false)
        private String receiverId;  // 수신자 ID

        @Column(nullable = false, length = 500)
        private String content;  // 메시지 내용

        private boolean isRead;  // 읽음 여부 (기본값은 false)

        // 채팅방 ID (선택 사항: 여러 명이 포함된 그룹 채팅을 지원하려면)
        private Long chatRoomId;

        // 메시지 생성 시 기본값 설정
        public Message(String senderId, String receiverId, String content, boolean isRead) {
                this.senderId = senderId;
                this.receiverId = receiverId;
                this.content = content;
                this.isRead = isRead;
        }

        public void setRead(boolean isRead) {
                this.isRead = isRead;
        }
}
