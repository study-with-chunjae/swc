package net.fullstack7.swc.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.constant.PostPageConstants;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.Share;
import net.fullstack7.swc.domain.ThumbUp;
import net.fullstack7.swc.dto.*;
import net.fullstack7.swc.mapper.PostMapper;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.repository.ShareRepository;
import net.fullstack7.swc.repository.ThumbUpRepository;
import net.fullstack7.swc.util.FileUploadUtil;
import net.fullstack7.swc.util.LogUtil;
import org.apache.ibatis.javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PostServiceImpl implements PostServiceIf {
    private final PostRepository postRepository;
    private final ShareRepository shareRepository;
    private final ThumbUpRepository thumbUpRepository;
    private final PostMapper postMapper;
    private final FileUploadUtil fileUploadUtil;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDateTime START_OF_TODAY = LocalDate.now().atStartOfDay();
    private final LocalDateTime END_OF_TODAY = LocalDate.now().plusDays(1).atStartOfDay();
    private final View error;

    @Override
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId){
        try {
            if(postRegisterDTO.getImage()!=null&&!postRegisterDTO.getImage().isEmpty()) {
                LogUtil.logLine("파일등록");
                LogUtil.log("file",postRegisterDTO.getImage());
                String imageFilePath = fileUploadUtil.uploadImageFile(postRegisterDTO.getImage(), "images");
                postRegisterDTO.setNewImagePath(imageFilePath);
            }
            if(postRegisterDTO.getTodayType()==1){
                validateDisplayDate(postRegisterDTO.getDisplayAt(), postRegisterDTO.getDisplayEnd());
            }
            Post post = Post.builder()
                    .title(postRegisterDTO.getTitle())
                    .content(postRegisterDTO.getContent())
                    .todayType(postRegisterDTO.getTodayType())
                    .createdAt(LocalDateTime.now())
                    .displayEnd(
                        postRegisterDTO.getDisplayEnd()!=null ? LocalDate.parse(postRegisterDTO.getDisplayEnd(),FORMATTER).atStartOfDay() : null
                    )
                    .displayAt(
                        postRegisterDTO.getDisplayAt()!=null ? LocalDate.parse(postRegisterDTO.getDisplayAt(),FORMATTER).atStartOfDay() : null
                    )
                    .hashtag(postRegisterDTO.getHashtag())
                    .image(postRegisterDTO.getNewImagePath()!=null?postRegisterDTO.getNewImagePath().replace("\\","/"):null)
                    .member(Member.builder().memberId(memberId).build())
                    .build();
            return postRepository.save(post);
        }catch(IllegalArgumentException e){
            log.error(e.getMessage());
            throw e;
        }
        catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
    @Transactional
    @Override
    public PostViewDTO viewPost(int postId) {
        try {
            //return modelMapper.map(postRepository.findById(postId).orElseThrow(), PostDTO.class);
            Post post = postRepository.viewPost(postId);
            return PostViewDTO.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent().replaceAll("\\r\\n?|\\n","<br>"))
                    .todayType(post.getTodayType())
                    .createdAt(post.getCreatedAt())
                    .displayAt(post.getDisplayAt())
                    .displayEnd(post.getDisplayEnd())
                    .image(post.getImage())
                    .member(Member.builder()
                            .memberId(post.getMember().getMemberId())
                            .name(post.getMember().getName())
                            .build()
                    )
                    .hashtags(post.getHashtag()!=null?Arrays.asList(post.getHashtag().split(",")):List.of())
                    .hashtagString(post.getHashtag()!=null?post.getHashtag():null)
                    .thumbUps(
                        post.getThumbUps().stream().map(t->{
                            return ThumbUp.builder()
                                    .thumbUpId(t.getThumbUpId())
                                    .post(Post.builder().postId(t.getPost().getPostId()).build())
                                    .member(Member.builder().memberId(t.getMember().getMemberId()).build())
                                    .build();
                        }).toList()
                    )
                    .shares(post.getShares().stream().map(s->{
                                return Share.builder()
                                        .shareId(s.getShareId())
                                        .createdAt(s.getCreatedAt())
                                        .post(Post.builder().postId(s.getPost().getPostId()).build())
                                        .member(Member.builder().memberId(s.getMember().getMemberId()).name(s.getMember().getName()).build())
                                        .build();
                            }).toList()
                    )
                    .build();
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
                    , START_OF_TODAY.plusDays(1)
                    , END_OF_TODAY.minusDays(1)
            );
            LogUtil.log("list", postList);
            return postList.stream().map(post -> PostMainDTO.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle().length()>13?post.getTitle().substring(0,12)+"...":post.getTitle())
                    .content(post.getContent().length()>400?
                            post.getContent().substring(0,400).concat("...").replaceAll("\\r\\n?|\\n","<br>") :
                            post.getContent().replaceAll("\\r\\n?|\\n","<br>")
                    )
                    .todayType(post.getTodayType())
                    .displayAt(post.getDisplayAt())
                    .displayEnd(post.getDisplayEnd())
                    .createdAt(post.getCreatedAt())
                    .hashtag(post.getHashtag()!=null?Arrays.asList(post.getHashtag().split(",")):List.of())
                    .image(post.getImage())
                    .shares(
                            post.getShares().stream().limit(5).map(s -> s.getMember().getMemberId()).toList()
                    )
                    .thumbUps(post.getThumbUps().size())
                    .build()).toList();
        }catch(Exception e){
            log.error("mainPost 에러 발생 : {}",e.getMessage());
            return null;
        }
    }

    @Override
    public List<PostMainDTO> mainPostList(LocalDateTime selectedDate, String memberId, Integer todayType) {
        LogUtil.logLine("PostService -> mainPostList");
        try{
            Member member = Member.builder().memberId(memberId).build();
            List<Post> postList = postRepository.findByMemberAndTodayTypeAndDisplayAtAfterAndDisplayEndBefore(member
                    , todayType
                    , selectedDate
                    , selectedDate
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
            List<PostDTO> pageDTOList = pagePost.getContent().stream().map(post -> {
                PostDTO postDTO = modelMapper.map(post, PostDTO.class);
                if(postDTO.getTitle().length()>15){
                    postDTO.setTitle(postDTO.getTitle().substring(0,15)+"...");
                }
                return postDTO;
            }).toList();
            validatedPageDTO.setDtoList(pageDTOList);
            return validatedPageDTO;
        }catch(Exception e){
            log.error("[[[sortAndSearch error : {}]]]", e.getMessage());
            return null;
        }
    }



//    @Override
//    public PageDTO<PostDTO> sortAndSearchShare(PageDTO<PostDTO> pageDTO, String memberId, String type) {
//        LogUtil.logLine("PostService -> sortAndSearchShare");
//        try{
//            PageDTO<PostDTO> validatedPageDTO = pageValid(pageDTO);
//            Page<Post> pagePost = postRepository.searchAndSortShare(validatedPageDTO.getPageable(),
//                    validatedPageDTO.getSearchField(),
//                    validatedPageDTO.getSearchValue(),
//                    validatedPageDTO.getSortField(),
//                    validatedPageDTO.getSortDirection(),
//                    validatedPageDTO.getSearchDateBegin(),
//                    validatedPageDTO.getSearchDateEnd(),
//                    memberId,
//                    type);
//            LogUtil.log("beforePostList",pagePost.getContent());
//            if(pagePost==null){
//                LogUtil.logLine("pagePost is null");
//                throw new NotFoundException("pagePost is null");
//            }
//            if(pagePost.getTotalPages()!=0 && validatedPageDTO.getPageNo() > pagePost.getTotalPages()) {
//                LogUtil.log("pageNo > totalPage",validatedPageDTO.getPageNo() +"//"+pagePost.getTotalPages());
//                validatedPageDTO.setPageNo(pagePost.getTotalPages());
//                pagePost = getPagePost(validatedPageDTO, memberId);
//                if (pagePost == null) throw new NotFoundException("pagePost is null");
//            }
//            validatedPageDTO.setTotalCount((int)pagePost.getTotalElements());
//            LogUtil.log("setTotalCount : ",
//                    validatedPageDTO.getTotalCount() +"//"+
//                            validatedPageDTO.getPageNo()+"//"+
//                            validatedPageDTO.getPageSize());
//            List<PostDTO> pageDTOList = pagePost.getContent().stream().map(post -> {
//                PostDTO postDTO = modelMapper.map(post, PostDTO.class);
//                if(postDTO.getTitle().length()>15){
//                    postDTO.setTitle(postDTO.getTitle().substring(0,15)+"...");
//                }
//                LogUtil.log("post",postDTO);
//                return postDTO;
//            }).toList();
//            LogUtil.log("list",pageDTOList);
//            validatedPageDTO.setDtoList(pageDTOList);
//            return validatedPageDTO;
//        }catch(Exception e){
//            LogUtil.log("[[sortAndSearch error : {}]]", e.getMessage());
//            return null;
//        }
//    }

    @Override
    public Post modifyPost(PostRegisterDTO postModifyDTO, String memberId) {
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
                if(post.getImage()!=null) {
                    log.info("delete file");
                    fileUploadUtil.deleteFile(post.getImage());
                    log.info("delete file success");
                }
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

    @Override
    public boolean deletePost(int postId, String memberId) throws IllegalArgumentException{
        LogUtil.logLine("PostService -> deletePost");
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학습입니다."));
            if (!post.getMember().getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("자신의 글만 삭제할 수 있습니다.");
            }
            if (post.getShares() != null && !post.getShares().isEmpty()) {
                shareRepository.deleteAll(post.getShares());
            }
            if (post.getImage() != null && !post.getImage().isEmpty()) {
                fileUploadUtil.deleteFile(post.getImage());
            }
            if (post.getThumbUps() != null && !post.getThumbUps().isEmpty()) {
                thumbUpRepository.deleteAll(post.getThumbUps());
            }
            postRepository.delete(post);
            return !postRepository.existsById(postId);
        }catch(IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public int totalCount(PageDTO<PostDTO> pageDTO, String memberId) {
        try{
            PageDTO<PostDTO> validatedPageDTO = pageValid(pageDTO);
            return postMapper.totalCount(validatedPageDTO.getSearchField(),
                    validatedPageDTO.getSearchValue(),
                    validatedPageDTO.getSortField(),
                    validatedPageDTO.getSortDirection(),
                    validatedPageDTO.getSearchDateBegin(),
                    validatedPageDTO.getSearchDateEnd(),
                    memberId
            );
        }catch(Exception e){
            log.error(e.getMessage());
            return -1;
        }

    }

    @Override
    public PageDTO<PostDTO> postList(PageDTO<PostDTO> pageDTO, String memberId) {
        LogUtil.logLine("PostService -> sortAndSearch");
        try {
            PageDTO<PostDTO> validatedPageDTO = pageValid(pageDTO);
            List<PostDTO> dtoList = postMapper.postList(
                    Map.of(
                            "searchField", validatedPageDTO.getSearchField(),
                            "searchValue", validatedPageDTO.getSearchValue(),
                            "sortField", validatedPageDTO.getSortField(),
                            "sortDirection", validatedPageDTO.getSortDirection(),
                            "searchDateBegin", validatedPageDTO.getSearchDateBegin(),
                            "searchDateEnd", LocalDate.parse(validatedPageDTO.getSearchDateEnd(),FORMATTER).plusDays(1).atStartOfDay(),
                            "memberId", memberId,
                            "offset",validatedPageDTO.getOffset(),
                            "pageSize", validatedPageDTO.getPageSize()
                    )
            ).stream()
                    .peek(postDTO -> {
                        if (postDTO.getTitle() != null && postDTO.getTitle().length() > 15) {
                            postDTO.setTitle(postDTO.getTitle().substring(0, 15)+"...");
                        }
                    }).toList();
            pageDTO.setDtoList(dtoList);
            return pageDTO;
        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public int shareTotalCount(PageDTO<PostDTO> pageDTO, String memberId, String type) {
        LogUtil.logLine("PostService -> shareTotalCount");
        try{
            PageDTO<PostDTO> validatedPageDTO = pageValid(pageDTO);
            return postMapper.sharedTotalCount(pageDTO,memberId,type);
        }catch(Exception e){
            log.error(e.getMessage());
            return -1;
        }
    }

    @Override
    public PageDTO<PostDTO> sortAndSearchShare(PageDTO<PostDTO> pageDTO, String memberId, String type) {
        LogUtil.logLine("PostService -> sortAndSearchShare");
        try {
            List<PostDTO> dtoList = postMapper.getSharedList(pageDTO,memberId,type).stream()
                    .peek(postDTO -> {
                        if (postDTO.getTitle() != null && postDTO.getTitle().length() > 15) {
                            postDTO.setTitle(postDTO.getTitle().substring(0, 15)+"...");
                        }
                    }).toList();
            pageDTO.setDtoList(dtoList);
            return pageDTO;
        }catch(Exception e){
            log.error("[[[getPagePost error : {}]]]",e.getMessage());
            return null;
        }
    }

    private Page<Post> getPagePost(PageDTO<PostDTO> pageDTO, String memberId){
        LogUtil.logLine("PostService -> getPagePost");
        try {
            return postRepository
                    .searchAndSort(pageDTO.getSortPageable(),
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
