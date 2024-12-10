package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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

        @Column(nullable = false)
        private String title;  // 쪽지 제목

        @Column(nullable = false, length = 500)
        private String content;  // 메시지 내용

        private boolean isRead;  // 읽음 여부 (기본값은 false)

        // 방인덱스(안쓸듯)
        private Long chatRoomId;

        @Column(columnDefinition = "datetime not null default now() comment '등록일'")
        private LocalDateTime regDate;
        
        @Column(columnDefinition = "datetime default now() comment '확인일'")
        private LocalDateTime confirmDate;

        // 메시지 생성 시 기본값 설정
        public Message(String senderId, String receiverId, String content, boolean isRead) {
                this.senderId = senderId;
                this.receiverId = receiverId;
                this.title = title;
                this.content = content;
                this.isRead = isRead;
                this.regDate = regDate;
                this.confirmDate = confirmDate;
        }

        public boolean isRead() {
                return isRead;
        }

        public void setRead(boolean isRead) {
                this.isRead = isRead;
                if(isRead){
                        this.confirmDate = LocalDateTime.now();
                }
        }

        public Message orElseThrow() {
                return null;
        }



}
