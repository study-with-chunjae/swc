package net.fullstack7.swc.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class QnaDTO {
    private Integer qnaId;
    private String title;
    private String question;
    private String email;    // 비회원용
    private String password; // 비회원용
    private String answer;
    private boolean answered;
}
