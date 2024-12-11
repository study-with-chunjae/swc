package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.service.MemberServiceImpl;
import net.fullstack7.swc.util.CookieUtil;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.domain.Member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.time.format.DateTimeFormatter;

@Log4j2
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageController {
  private final MemberServiceImpl memberService;
  private final JwtTokenProvider jwtTokenProvider;
  private final CookieUtil cookieUtil;

  private String getMemberIdInJwt(HttpServletRequest req){
    String accessToken = cookieUtil.getCookieValue(req,"accessToken");
    return memberService.getMemberInfo(accessToken).get("memberId");
}

  @GetMapping("/info")
  public String myPage(Model model, HttpServletRequest req) {
    try {
      String memberId = getMemberIdInJwt(req);
      log.info("memberId : {}", memberId);

      Member member = memberService.getMemberById(memberId);

      model.addAttribute("name", member.getName());
      model.addAttribute("email", member.getEmail());
      model.addAttribute("phone", member.getPhone());
      model.addAttribute("myInfo", member.getMyInfo());

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a h시 mm분");
      String lastLoginFormatted = member.getLastLoginAt() != null ? member.getLastLoginAt().format(formatter) : "없음";
      String updatedAtFormatted = member.getUpdatedAt() != null ? member.getUpdatedAt().format(formatter) : "없음";

      model.addAttribute("lastLogin", lastLoginFormatted);
      model.addAttribute("updatedAt", updatedAtFormatted);

      return "myPage/myPageInfo";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/followList")
  public String myPageFollowList() {
    return "myPage/myPageFollowList";
  }

  @GetMapping("/friend")
  public String myPageFriend() {
    return "myPage/myPageFriend";
  }

  @GetMapping("/message")
  public String myPageMessage() {
    return "myPage/myPageMsg";
  }
}
