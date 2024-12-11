package net.fullstack7.swc.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.*;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class PostRegisterDTO {
    private Integer postId;
    @NotBlank(message="제목을 입력하세요")
    @Size(max=100, message="100글자 이하로 입력하세요")
    private String title;
    @NotBlank(message="내용을 입력하세요")
    @Size(max=2000, message="2000글자 이하로 입력하세요")
    private String content;
    @Builder.Default
    @Max(1)
    @Min(-1)
    private Integer todayType=-1; //오늘의 학습노출여부(0:비공개 1:공개)

    private String displayAt; //오늘의 학습노출시작일
    private String displayEnd; //오늘의 학습노출종료일
    private LocalDateTime createdAt; //게시글 생성일

    @Size(max=100, message="100글자 이하로 입력하세요")
    private String topics; //분야
    @Size(max=47, message="해시태그 하나당 10자 이하로 입력하세요")
    private String hashtag; //해시태그

    private MultipartFile image;
    private String newImagePath;
    public String getTitle(){
        return StringEscapeUtils.escapeHtml4(title);
    }
    public String getContent(){
        return StringEscapeUtils.escapeHtml4(content);
    }
    public String getTopics(){
        return StringEscapeUtils.escapeHtml4(topics);
    }
    public String getHashtag(){
        return StringEscapeUtils.escapeHtml4(hashtag);
    }
}
