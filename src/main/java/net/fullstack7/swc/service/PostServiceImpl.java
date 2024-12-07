package net.fullstack7.swc.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.constant.PostPageConstants;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.*;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.util.FileUploadUtil;
import net.fullstack7.swc.util.LogUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PostServiceImpl implements PostServiceIf {
    private final PostRepository postRepository;
    private final FileUploadUtil fileUploadUtil;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDateTime START_OF_TODAY = LocalDate.now().atStartOfDay();
    private final LocalDateTime END_OF_TODAY = LocalDate.now().plusDays(1).atStartOfDay();
    private final View error;

    @Override
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId){
        try {
            String imageFilePath = fileUploadUtil.uploadImageFile(postRegisterDTO.getFile(), "images");
            if(postRegisterDTO.getTodayType()==1){
                validateDisplayDate(postRegisterDTO.getDisplayAt(), postRegisterDTO.getDisplayEnd());
            }
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
    public PostDTO viewPost(int postId) {
        try {
            return modelMapper.map(postRepository.findById(postId).orElseThrow(), PostDTO.class);
        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<PostMainDTO> mainPost(LocalDateTime createdAt, String memberId, Integer todayType) {
        LogUtil.logLine("PostService -> mainPost");
        try {
            Member member = Member.builder().memberId(memberId).build();
            List<Post> postList = postRepository.findByMemberAndTodayTypeAndCreatedAtBetweenAndDisplayAtBeforeAndDisplayEndAfter(member
                    , todayType
                    , createdAt
                    , createdAt.plusDays(1)
                    , START_OF_TODAY
                    , END_OF_TODAY
            );
            LogUtil.log("list", postList);
            return postList.stream().map(post -> modelMapper.map(post, PostMainDTO.class)).toList();
        }catch(Exception e){
            log.error("mainPost 에러 발생 : {}",e.getMessage());
            return null;
        }
    }

    @Override
    public PageDTO<PostDTO> sortAndSearch(PageDTO<PostDTO> pageDTO, String memberId) {
        LogUtil.logLine("PostService -> sortAndSearch");
        try {
            PageDTO<PostDTO> validatedPageDTO = pageValid(pageDTO);
            Page<Post> pagePost = getPagePost(validatedPageDTO, memberId);

            if (pagePost == null) {
                LogUtil.logLine("pagePost is null");
                throw new AssertionError();
            }

            if (pagePost.getTotalPages()!=0 && validatedPageDTO.getPageNo() > pagePost.getTotalPages()) {
                LogUtil.log("pageNo > totalPage",validatedPageDTO.getPageNo() +"//"+pagePost.getTotalPages());
                validatedPageDTO.setPageNo(pagePost.getTotalPages());
                pagePost = getPagePost(validatedPageDTO, memberId);
                if (pagePost == null) throw new AssertionError();
            }
            validatedPageDTO.setTotalCount((int)pagePost.getTotalElements());
            LogUtil.log("setTotalCount : ",
                    validatedPageDTO.getTotalCount() +"//"+
                          validatedPageDTO.getPageNo()+"//"+
                          validatedPageDTO.getPageSize());
            List<PostDTO> pageDTOList = pagePost.getContent().stream().map(post -> modelMapper.map(post, PostDTO.class)).toList();
            validatedPageDTO.setDtoList(pageDTOList);
            return validatedPageDTO;
        }catch(Exception e){
            log.error("[[[sortAndSearch error : {}]]]", e.getMessage());
            return null;
        }
    }

    @Override
    public Post modifyPost(PostModifyDTO postModifyDTO, String memberId) {
        LogUtil.logLine("PostService -> modifyPost");
        try {
            Post post = postRepository.findById(postModifyDTO.getPostId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 학습입니다."));
            if(!post.getMember().getMemberId().equals(memberId)){
                log.info("수정권한없음");
                throw new IllegalArgumentException("수정 권한이 없습니다.");
            }

            if(postModifyDTO.getTodayType()==1){
                log.info("todayType == 1");
                validateDisplayDate(postModifyDTO.getDisplayAt(), postModifyDTO.getDisplayEnd());
            }

            if(postModifyDTO.getImage()!=null){
                log.info("new file exist");
                log.info("delete file");
                fileUploadUtil.deleteFile(post.getImage());
                log.info("delete file success");
                log.info("upload new file");
                String newImagePath =fileUploadUtil.uploadImageFile(postModifyDTO.getImage(),"images");
                log.info("upload new file success");
                postModifyDTO.setNewImagePath(newImagePath);
            }

            return postRepository.save(
                Post.builder()
                    .postId(postModifyDTO.getPostId())
                    .title(postModifyDTO.getTitle())
                    .content(postModifyDTO.getContent())
                    .todayType(postModifyDTO.getTodayType())
                    .displayAt(LocalDate.parse(postModifyDTO.getDisplayAt(),FORMATTER).atStartOfDay())
                    .displayEnd(LocalDate.parse(postModifyDTO.getDisplayEnd(),FORMATTER).atStartOfDay())
                    .hashtag(postModifyDTO.getHashtag())
                    .topics(postModifyDTO.getTopics())
                    .image(postModifyDTO.getNewImagePath())
                    .member(Member.builder().memberId(memberId).build())
                    .createdAt(post.getCreatedAt())
                    .build()
            );
        }catch(IllegalArgumentException e){
            LogUtil.log("잘못된 형식",e.getMessage());
            return null;
        }
        catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    private Page<Post> getPagePost(PageDTO<PostDTO> pageDTO, String memberId){
        LogUtil.logLine("PostService -> getPagePost");
        try {
            return postRepository.searchAndSort(pageDTO.getPageable(),
                    pageDTO.getSearchField(),
                    pageDTO.getSearchValue(),
                    pageDTO.getSortField(),
                    pageDTO.getSortDirection(),
                    pageDTO.getSearchDateBegin(),
                    pageDTO.getSearchDateEnd(),
                    memberId);
        }catch(Exception e){
            log.error("[[[getPagePost error : {}]]]",e.getMessage());
            return null;
        }
    }
    private PageDTO<PostDTO> pageValid(PageDTO<PostDTO> pageDTO){
        LogUtil.logLine("PostService -> pageValid");
        StringBuilder errorMessage = new StringBuilder();
        try {
            if (!PostPageConstants.ALLOWED_SEARCH_FIELDS.contains(pageDTO.getSearchField())) {
                errorMessage.append("잘못된 검색조건입니다.").append(System.lineSeparator());
                pageDTO.setSearchField(PostPageConstants.DEFAULT_SEARCH_FIELD);
                pageDTO.setSearchValue(PostPageConstants.DEFAULT_SEARCH_FIELD);

            } else if (!PostPageConstants.ALLOWED_SORT_FIELDS.contains(pageDTO.getSortField())) {
                errorMessage.append("잘못된 정렬조건입니다.").append(System.lineSeparator());
                pageDTO.setSortField(PostPageConstants.DEFAULT_SORT_FIELD);

            } else if (!PostPageConstants.ALLOWED_SORT_ORDER.contains(pageDTO.getSortDirection())) {
                errorMessage.append("잘못된 정렬방식입니다.").append(System.lineSeparator());
                pageDTO.setSortDirection(PostPageConstants.DEFAULT_SORT_ORDER);

            } else if (LocalDate.parse(pageDTO.getSearchDateBegin(), FORMATTER).isAfter(LocalDate.parse(pageDTO.getSearchDateEnd(), FORMATTER))) {
                errorMessage.append("검색시작일이 검색종료일보다 클수 없습니다.").append(System.lineSeparator());
                pageDTO.setSearchDateEnd(PostPageConstants.DEFAULT_SEARCH_DATE_END.format(FORMATTER));
                pageDTO.setSearchDateBegin(PostPageConstants.DEFAULT_SEARCH_DATE_BEGIN.format(FORMATTER));
            }
            LogUtil.log("pageValid errorMessage", errorMessage);
            return pageDTO;
        }catch(Exception e){
            LogUtil.log("pageValid exception errorMessage", errorMessage.append(e.getMessage()));
            pageDTO.setSearchField(PostPageConstants.DEFAULT_SEARCH_FIELD);
            pageDTO.setSearchValue(PostPageConstants.DEFAULT_SEARCH_FIELD);
            pageDTO.setSortField(PostPageConstants.DEFAULT_SORT_FIELD);
            pageDTO.setSortDirection(PostPageConstants.DEFAULT_SORT_ORDER);
            pageDTO.setSearchDateEnd(PostPageConstants.DEFAULT_SEARCH_DATE_END.format(FORMATTER));
            pageDTO.setSearchDateBegin(PostPageConstants.DEFAULT_SEARCH_DATE_BEGIN.format(FORMATTER));
            return pageDTO;
        }
    }
    private void validateDisplayDate(String displayAt, String displayEnd){
        LogUtil.logLine("validateDisplayDate");
        if(displayAt==null||displayEnd==null){
            throw new IllegalArgumentException("노출시작일과 종료일을 모두 입력하세요.");
        }
        if(LocalDate.parse(displayAt, FORMATTER).isAfter(LocalDate.parse(displayEnd, FORMATTER))){
            throw new IllegalArgumentException("노출 시작일이 노출 종료일보다 클 수 없습니다.");
        }
        if(START_OF_TODAY.isAfter(LocalDate.parse(displayEnd, FORMATTER).atStartOfDay())){
            throw new IllegalArgumentException("노출 종료일이 오늘보다 이전일 수 없습니다.");
        }
    }
}
