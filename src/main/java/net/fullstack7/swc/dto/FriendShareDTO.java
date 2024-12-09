package net.fullstack7.swc.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class FriendShareDTO {
    private Integer friendId;
    private Member receiver; //친구아이디
    private Member requester; //친구요청한사람 아이디
    private Integer status; //수락아직안함 0 수락 1
}
