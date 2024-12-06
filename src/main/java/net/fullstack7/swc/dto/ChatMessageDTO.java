package net.fullstack7.swc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private int chatMessageId;
    private Integer chatRoomId;
    private Integer isRead;
    private LocalDateTime createdAt;
    private String senderId;
    private String message;

}
