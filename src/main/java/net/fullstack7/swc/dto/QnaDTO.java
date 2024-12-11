package net.fullstack7.swc.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Qna;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class QnaDTO {
    private Integer qnaId;
    private String title;
    private String content;
    private String email;    // 비회원용
    private String password; // 비회원용
    private boolean answered;
    private Integer parentId;
    private List<QnaDTO> replies = new ArrayList<>();
    private LocalDateTime regDate;
}
