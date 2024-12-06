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
public class ChatRoomDTO {
    private int chatRoomId;
    private Integer receiverStatus;
    private Integer senderStatus;
    private String senderId;
    private String receiverId;

}
