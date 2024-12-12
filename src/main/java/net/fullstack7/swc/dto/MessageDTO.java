package net.fullstack7.swc.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class MessageDTO {
    private Long id;  // 메시지 ID
    private String senderId;  // 발신자 ID
    private String receiverId;  // 수신자 ID
    private String title;  // 쪽지 제목
    private String content;  // 메시지 내용
    private boolean isRead;  // 읽음 여부 (기본값은 false)
    private Long chatRoomId;
    private LocalDateTime regDate;
    private LocalDateTime confirmDate;

}
