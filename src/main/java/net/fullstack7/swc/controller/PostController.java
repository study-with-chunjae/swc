package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.constant.PostPageConstants;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.MemberProfile;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.*;
import net.fullstack7.swc.repository.MemberProfileRepository;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.PostServiceIf;
import net.fullstack7.swc.service.ThumbUpServiceIf;
import net.fullstack7.swc.util.CheckJwtToken;
import net.fullstack7.swc.util.CookieUtil;
import net.fullstack7.swc.util.ErrorUtil;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private static final String CONTROLLER_NAME = "PostController";
    private static final String DEFAULT_REDIRECT = "/post/list";
    private static final Integer TYPE_SHARE = 1;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String NOW_STRING = FORMATTER.format(LocalDate.now());
    private final MemberServiceIf memberService;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PostServiceIf postService;
    private final ThumbUpServiceIf thumbUpService;
    private final ErrorUtil errorUtil;
    private final CookieUtil cookieUtil;

    @CheckJwtToken
    @GetMapping("/main")
    public String postMain(@RequestParam(required = false, defaultValue="") String memberId,
                           @RequestParam(required = false, defaultValue = "") String createdAt,
                           HttpServletRequest req, Model model, RedirectAttributes redirectAttributes) {
        LogUtil.logLine(CONTROLLER_NAME + "main");
        if(memberId.isEmpty()){
            memberId = cookieUtil.getMemberIdInJwt(memberService, req);
        }
        if(createdAt.isEmpty()){
            createdAt = NOW_STRING;
        }
        try {
            LogUtil.log("now", createdAt);
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalAccessError("존재하지 않는 회원입니다"));
            MemberDTO memberDTO = MemberDTO.builder()
                    .memberId(memberId)
                    .name(member.getName())
                    .myInfo(member.getMyInfo())
                    .build();
            List<PostMainDTO> postMainDTOList = postService.mainPost(LocalDate.parse(createdAt, FORMATTER).atStartOfDay(), memberId, TYPE_SHARE);
            if (postMainDTOList == null) {
                model.addAttribute("error", "조회 중 일시적인 에러가 발생했습니다.");
            }
            model.addAttribute("createdAt", createdAt);
            model.addAttribute("month", createdAt.split("-")[1]);
            model.addAttribute("week", getWeekOfMonth(createdAt));
            model.addAttribute("date", createdAt.split("-")[2]);
            model.addAttribute("viewType", "today");
            model.addAttribute("postMainDTOList", postMainDTOList);
            model.addAttribute("memberDTO", memberDTO);
            model.addAttribute("profileImage", memberProfileRepository.findByMember(member).orElse(null));
            return "main/main";
        }catch(Exception e){
            log.error(e.getMessage());
            return errorUtil.redirectWithError(e.getMessage(),"/",redirectAttributes);
        }
    }

    @CheckJwtToken
    @GetMapping("/list")
    public String list(@Valid PageDTO<PostDTO> pageDTO,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model,
                       HttpServletRequest req) {
        LogUtil.logLine(CONTROLLER_NAME + "list");
        if(bindingResult.hasErrors()){
            pageDTO = PageDTO.<PostDTO>builder().build();
        }
        pageDTO.initialize(PostPageConstants.DEFAULT_SORT_FIELD, PostPageConstants.DEFAULT_SORT_ORDER);
        String memberId = getMemberIdInJwt(req);
        int totalCount = postService.totalCount(pageDTO,memberId);
        pageDTO.setTotalCount(totalCount);
        pageDTO = postService.postList(pageDTO,memberId);
        model.addAttribute("pageDTO",pageDTO);
        model.addAttribute("viewType","my");
        return "todo/mylist";
    }

    @CheckJwtToken
    @GetMapping("/sharelist")
    public String shareList(@RequestParam(required = false, defaultValue = "") String type,
                            @Valid PageDTO<PostDTO> pageDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest req) {
        LogUtil.logLine(CONTROLLER_NAME + "shareList");
        if(bindingResult.hasErrors()){
            pageDTO = PageDTO.<PostDTO>builder().build();
        }
        type = validShareListType(type);
        String memberId = getMemberIdInJwt(req);
        pageDTO.initialize(PostPageConstants.DEFAULT_SORT_FIELD, PostPageConstants.DEFAULT_SORT_ORDER);
        LogUtil.log("totalCount",postService.shareTotalCount(pageDTO,memberId,type));
        pageDTO.setTotalCount(postService.shareTotalCount(pageDTO,memberId,type));
        pageDTO = postService.sortAndSearchShare(pageDTO,memberId,type);
        LogUtil.log("searchDateBegin",pageDTO.getSearchDateBegin());
        LogUtil.log("searchDateEnd",pageDTO.getSearchDateEnd());
        LogUtil.log("offset",pageDTO.getOffset());
        LogUtil.log("pageSize",pageDTO.getPageSize());
        LogUtil.log("totalCount",pageDTO.getTotalCount());
        LogUtil.log("totalPage",pageDTO.getTotalPage());
        model.addAttribute("pageDTO",pageDTO);
        model.addAttribute("type",type);
        model.addAttribute("viewType","share");
        return "todo/sharelist";
    }

    private String validShareListType(String type){
        if(type.isEmpty()){
            return "my";
        }
        if(!type.equals("my") && !type.equals("others")){
            return "my";
        }
        return type;
    }

    @CheckJwtToken
    @GetMapping("/view")
    public String view(@RequestParam(required = false, defaultValue="-1") int postId,
                       @RequestParam(required = false, defaultValue = "") String queryString,
                       HttpServletRequest req,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        LogUtil.logLine(CONTROLLER_NAME + "view");
        if(postId < 0){
            return errorUtil.redirectWithError("잘못된 값이 입력되었습니다.", DEFAULT_REDIRECT,redirectAttributes);
        }
        String memberId = getMemberIdInJwt(req);
        PostViewDTO postDTO = postService.viewPost(postId);
        LogUtil.log("postDTO",postDTO);
        LogUtil.log("shares",postDTO.getShares());
        LogUtil.log("hashtags",postDTO.getHashtags());
        LogUtil.log("thumbUps",postDTO.getThumbUps());
        LogUtil.log("queryString",queryString);
        model.addAttribute("viewType",getViewType(req));
        model.addAttribute("postDTO",postDTO);
        model.addAttribute("thumbUp",thumbUpService.isExist(postId,memberId)?"1":"0");
        model.addAttribute("queryString", URLDecoder.decode(URLDecoder.decode(queryString, StandardCharsets.UTF_8),StandardCharsets.UTF_8));
        return "todo/view";
    }

    @CheckJwtToken(redirectUri = DEFAULT_REDIRECT)
    @GetMapping("/register")
    public String registerGet(HttpServletRequest req, RedirectAttributes redirectAttributes, Model model) {
        LogUtil.logLine(CONTROLLER_NAME + "registerGet");
        String accessToken = cookieUtil.getCookieValue(req,"accessToken");
        Map<String,String> memberInfo = memberService.getMemberInfo(accessToken);
//        if(memberInfo == null) {
//            return redirectWithError("로그인 정보 없음",DEFAULT_REDIRECT,redirectAttributes);
//        }
        MemberDTO memberDTO = MemberDTO.builder()
                .memberId(memberInfo.get("memberId"))
                .name(memberInfo.get("name"))
                .build();
        model.addAttribute("memberDTO", memberDTO);
        return "todo/regist";
    }

    @CheckJwtToken(redirectUri = DEFAULT_REDIRECT)
    @PostMapping("/register")
    public String registerPost(@Valid PostRegisterDTO postRegisterDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest req, Model model){
        LogUtil.logLine(CONTROLLER_NAME + "registerPost");
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("postDTO", postRegisterDTO);
            return errorUtil.redirectWithError("/post/register",redirectAttributes,bindingResult);
        }
        try {
            String memberId = getMemberIdInJwt(req);
            Post post = postService.registerPost(postRegisterDTO, memberId);
            if (post == null) {
                redirectAttributes.addFlashAttribute("postDTO", postRegisterDTO);
                return errorUtil.redirectWithError("게시글등록 실패", "/post/register", redirectAttributes);
            }
            return "redirect:/post/view?postId=" + post.getPostId();
        }catch(Exception e){
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("postDTO", postRegisterDTO);
            return errorUtil.redirectWithError(e.getMessage(),"/post/register", redirectAttributes);
        }
    }

    @CheckJwtToken
    @GetMapping("/modify")
    public String modifyGet(@RequestParam(required = false, defaultValue = "-1") int postId,
                            HttpServletRequest req,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        LogUtil.logLine(CONTROLLER_NAME + "modify");
        if(postId < 0){
            return errorUtil.redirectWithError("잘못된 값이 입력되었습니다.",DEFAULT_REDIRECT,redirectAttributes);
        }
        String memberId = getMemberIdInJwt(req);
        PostViewDTO postDTO = postService.viewPost(postId);
        if(postDTO==null){
            return errorUtil.redirectWithError("없는 게시글입니다.",DEFAULT_REDIRECT,redirectAttributes);
        }
        if(!postDTO.getMember().getMemberId().equals(memberId)){
            return errorUtil.redirectWithError("권한이 없습니다",DEFAULT_REDIRECT,redirectAttributes);
        }
        postDTO.setContent(postDTO.getContent().replace("<br>","&#13;&#10;"));
        LogUtil.log("postDTO",postDTO);
        LogUtil.log("shares",postDTO.getShares());
        LogUtil.log("hashtags",postDTO.getHashtags());
        LogUtil.log("thumbUps",postDTO.getThumbUps());
        model.addAttribute("postDTO",postDTO);
        return "todo/modify";
    }
    @PostMapping("/modify")
    public String modifyPost(@Valid PostRegisterDTO postModifyDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest req,
                             Model model){
        LogUtil.logLine(CONTROLLER_NAME + "modify post");
        if(bindingResult.hasErrors()){
            return errorUtil.redirectWithError(DEFAULT_REDIRECT,redirectAttributes,bindingResult);
        }
        String memberId = getMemberIdInJwt(req);
        Post post = postService.modifyPost(postModifyDTO, memberId);
        if(post==null){
            return errorUtil.redirectWithError("게시글수정 실패",DEFAULT_REDIRECT,redirectAttributes);
        }
        return "redirect:/post/view?postId="+post.getPostId();
    }

    private String getMemberIdInJwt(HttpServletRequest req){
        String accessToken = cookieUtil.getCookieValue(req,"accessToken");
        return memberService.getMemberInfo(accessToken).get("memberId");
    }
    private void validTitle(PostRegisterDTO postRegisterDTO){
        String title = postRegisterDTO.getTitle();

    }
    private int getWeekOfMonth(String dateStr){
        LocalDate date = LocalDate.parse(dateStr,FORMATTER);
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.get(weekFields.weekOfMonth());
    }
    private String getViewType(HttpServletRequest req){
        String referer = req.getHeader("referer");
        if(referer.contains("share")){
            return "share";
        }else{
            return "my";
        }
    }
}
