package net.fullstack7.swc.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class ShareDTO {
    private Integer shareId;
    private LocalDateTime createdAt;
    private String memberId; //공유받은사람아이디
    private Integer postId; //게시글 인덱스
}
