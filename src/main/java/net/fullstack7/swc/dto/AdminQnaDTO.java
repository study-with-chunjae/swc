package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class AdminQnaDTO {
    private Integer qnaId;
    @NotBlank(message="제목을 입력하세요")
    @Size(max=100, message="100글자 이하로 입력하세요")
    private String title;
    @NotBlank(message="내용을 입력하세요")
    @Size(max=1000, message="1000글자 이하로 입력하세요")
    private String content;
    private String password; // 비회원용
    private boolean answered;
    private Integer parentId;
    private List<AdminQnaDTO> replies = new ArrayList<>();
    private LocalDateTime regDate;
}
