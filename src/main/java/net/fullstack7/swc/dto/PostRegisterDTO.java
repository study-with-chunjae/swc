package net.fullstack7.swc.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.*;
import net.fullstack7.swc.util.LogUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class PostRegisterDTO {
    @Builder.Default
    private final LocalDate NOW = LocalDate.now();
    @Builder.Default
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Integer postId;
    @NotBlank(message="제목을 입력하세요")
    @Size(max=100, message="100글자 이하로 입력하세요")
    private String title;
    @NotBlank(message="내용을 입력하세요")
    @Size(max=2000, message="2000글자 이하로 입력하세요")
    private String content;
    @Builder.Default
    private Integer todayType=-1; //오늘의 학습노출여부(0:비공개 1:공개)

    private String displayAt; //오늘의 학습노출시작일
    private String displayEnd; //오늘의 학습노출종료일
    private LocalDateTime createdAt; //게시글 생성일

    @Size(max=100, message="100글자 이하로 입력하세요")
    private String topics; //분야
    @Size(max=27, message="해시태그 하나당 10자 이하로 입력하세요")
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

    @AssertTrue(message="시작일은 종료일 이후일 수 없습니다.")
    public boolean displayAtAndEndValid(){
        LogUtil.logLine("displayAtAndEndValid");
        return !LocalDate.parse(getDisplayAt(), FORMATTER).isAfter(LocalDate.parse(displayEnd, FORMATTER));
    }
    @AssertTrue(message="종료일은 현재 날짜 이후일 수 없습니다.")
    public boolean displayEndValid(){
        LogUtil.logLine("displayEndValid");
        return !LocalDate.parse(getDisplayEnd(), FORMATTER).isAfter(NOW);
    }
    @AssertTrue(message="해시태그 형식이 잘못되었거나 조건을 만족하지 않습니다.")
    public boolean validHashtag(){
        LogUtil.logLine("validHashtag");
        if(hashtag==null) return true;
        final String regex = "^(#\\\\w{1,10})(,#[^,]{1,10}){0,3}$";
        java.util.regex.Pattern pattern = Pattern.compile(regex);
        if(!pattern.matcher(hashtag).matches()){
            return false;
        }
        String[] items = hashtag.split(",");
        Set<String> set = new HashSet<>(Arrays.asList(items));
        return set.size() <= 4 && set.size() == items.length;
    }
    @AssertTrue(message="잘못된 노출여부 입력값입니다.")
    public boolean validTodayType(){
        LogUtil.logLine("validTodayType");
        try {
            return todayType > -1 && todayType < 2;
        }catch(Exception e){
            log.error("validTodayType error: {}",e.getMessage());
            return false;
        }
    }
    @AssertTrue(message="잘못된 입력값입니다.")
    public boolean validPostId(){
        LogUtil.logLine("validPostId");
        try{
            return postId > -1;
        }catch(Exception e){
            log.error("validPostId error: {}",e.getMessage());
            return false;
        }
    }
}
