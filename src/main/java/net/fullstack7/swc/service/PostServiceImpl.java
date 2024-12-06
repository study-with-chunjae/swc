package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PostRegisterDTO;
import net.fullstack7.swc.dto.PostViewDTO;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PostServiceImpl implements PostServiceIf {
    private final PostRepository postRepository;
    private final FileUploadUtil fileUploadUtil;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId){
        try {
            String imageFilePath = fileUploadUtil.uploadImageFile(postRegisterDTO.getFile(), "images");
            Post post = Post.builder()
                    .title(postRegisterDTO.getTitle())
                    .content(postRegisterDTO.getContent())
                    .todayType(postRegisterDTO.getTodayType())
                    .createdAt(postRegisterDTO.getCreatedAt())
                    .displayEnd(LocalDate.parse(postRegisterDTO.getDisplayEnd(),FORMATTER).atStartOfDay())
                    .topics(postRegisterDTO.getTopics())
                    .displayAt(LocalDate.parse(postRegisterDTO.getDisplayAt(),FORMATTER).atStartOfDay())
                    .hashtag(postRegisterDTO.getHashtag())
                    .image(imageFilePath.replace("\\","/"))
                    .member(Member.builder().memberId(memberId).build())
                    .build();
            return postRepository.save(post);
        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public PostViewDTO viewPost(int postId) {
        return null;
    }
}
