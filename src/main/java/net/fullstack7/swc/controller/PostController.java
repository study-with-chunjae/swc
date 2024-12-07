package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.constant.PostPageConstants;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.*;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.PostServiceIf;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    private final PostServiceIf postService;
    private final ErrorUtil errorUtil;
    private final CookieUtil cookieUtil;

    @CheckJwtToken
    @GetMapping("/main")
    public String postMain(@RequestParam(required = false, defaultValue = "") String createdAt,
                           HttpServletRequest req, Model model, RedirectAttributes redirectAttributes) {
        LogUtil.logLine(CONTROLLER_NAME + "main");
        if(createdAt.isEmpty()){
            createdAt = NOW_STRING;
        }
        String memberId = getMemberIdInJwt(req);
        List<PostMainDTO> postMainDTOList = postService.mainPost(LocalDate.parse(createdAt,FORMATTER).atStartOfDay(),memberId,TYPE_SHARE);
        if(postMainDTOList==null){
            model.addAttribute("error","조회 중 일시적인 에러가 발생했습니다.");
        }
        model.addAttribute("postMainDTOList",postMainDTOList);
        return "post/main";
    }

    @CheckJwtToken
    @GetMapping("/list")
    public String list(@Valid PageDTO<PostDTO> pageDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, HttpServletRequest req) {
        LogUtil.logLine(CONTROLLER_NAME + "list");
        if(bindingResult.hasErrors()){
            pageDTO = PageDTO.<PostDTO>builder().build();
        }
        pageDTO.initialize(PostPageConstants.DEFAULT_SORT_FIELD, PostPageConstants.DEFAULT_SORT_ORDER);
        String memberId = getMemberIdInJwt(req);
        pageDTO = postService.sortAndSearch(pageDTO,memberId);
        model.addAttribute("pageDTO",pageDTO);
        return "post/list";
    }

    @CheckJwtToken
    @GetMapping("/view")
    public String view(@RequestParam(required = false, defaultValue="-1") int postId,
                       HttpServletRequest req, Model model, RedirectAttributes redirectAttributes) {
        LogUtil.logLine(CONTROLLER_NAME + "view");
        String memberId = getMemberIdInJwt(req);

        return "post/view";
    }

    @CheckJwtToken(redirectUri = DEFAULT_REDIRECT)
    @GetMapping("/register")
    public String registerGet(HttpServletRequest req, RedirectAttributes redirectAttributes, Model model) {
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
        return "post/register";
    }

    @CheckJwtToken(redirectUri = DEFAULT_REDIRECT)
    @PostMapping("/register")
    public String registerPost(@Valid PostRegisterDTO postRegisterDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest req, Model model){
        LogUtil.logLine(CONTROLLER_NAME + "register");
        if(bindingResult.hasErrors()){
            return errorUtil.redirectWithError(DEFAULT_REDIRECT,redirectAttributes,bindingResult);
        }
        String memberId = getMemberIdInJwt(req);
        Post post = postService.registerPost(postRegisterDTO, memberId);
        if(post==null){
            return errorUtil.redirectWithError("게시글등록 실패",DEFAULT_REDIRECT,redirectAttributes);
        }
        return "redirect:post/list";
    }

    @GetMapping("/modify")
    public String modifyGet() {
        LogUtil.logLine(CONTROLLER_NAME + "modify");
        return "post/modify";
    }
    @PostMapping("/modify")
    public String modifyPost(){
        LogUtil.logLine(CONTROLLER_NAME + "modify post");
        return "post/view";
    }

    private String getMemberIdInJwt(HttpServletRequest req){
        String accessToken = cookieUtil.getCookieValue(req,"accessToken");
        return memberService.getMemberInfo(accessToken).get("memberId");
    }
}
