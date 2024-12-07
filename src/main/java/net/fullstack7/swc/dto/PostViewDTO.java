package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class PostViewDTO {
    private Integer postId;
    private String title;
    private String content;
    private Integer todayType; //오늘의 학습노출여부(0:비공개 1:공개)
    private LocalDateTime displayAt; //오늘의 학습노출시작일
    private LocalDateTime displayEnd; //오늘의 학습노출시작일
    private LocalDateTime createdAt; //게시글 생성일
    private String topics; //분야
    private String hashtag; //해시태그
    private String image;
    private Integer thumbUpsCount;
    private List<String> sharedMemberList;
}
