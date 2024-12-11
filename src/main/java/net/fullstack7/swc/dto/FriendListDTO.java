package net.fullstack7.swc.dto;

import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FriendListDTO {
    private Integer friendId;
    private String myId; // 수락할사람
    private String myName;
    private String othersId; // 신청자
    private String othersName;
    private Integer status;
    private LocalDateTime regDate;
}
