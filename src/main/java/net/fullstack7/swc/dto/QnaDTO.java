package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message="제목을 입력하세요")
    @Size(max=100, message="100글자 이하로 입력하세요")
    private String title;
    @NotBlank(message="내용을 입력하세요")
    @Size(max=1000, message="1000글자 이하로 입력하세요")
    private String content;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;    // 비회원용
    @NotBlank(message="비밀번호를 입력하세요")
    @Size(min=1, max=20, message="1~20자 이내로 입력하세요")
    private String password; // 비회원용
    private boolean answered;
    private Integer parentId;
    @Builder.Default
    private List<QnaDTO> replies = new ArrayList<>();
    private LocalDateTime regDate;
}
