package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Share;
import net.fullstack7.swc.domain.ThumbUp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class PostModifyDTO {
    @Min(-1)
    @Builder.Default
    private Integer postId=-1;
    @NotBlank(message="제목은 필수입니다.")
    @Size(max=100, message="100자 이하로 입력하세요")
    private String title;
    @NotBlank(message="내용은 필수입니다.")
    @Size(max=2000, message="2000자 이내로 입력하세요")
    private String content;
    @Builder.Default
    @Max(1)
    @Min(-1)
    private Integer todayType=-1; //오늘의 학습노출여부(0:비공개 1:공개)
    private String displayAt; //오늘의 학습노출시작일
    private String displayEnd; //오늘의 학습노출시작일
    private String topics; //분야
    private String hashtag; //해시태그
    private MultipartFile image;
    private String newImagePath;
    private Member member;
}
