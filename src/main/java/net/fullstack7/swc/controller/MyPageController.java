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

      String name = member.getName() != null ? member.getName() : "비공개";
      String email = member.getEmail() != null ? member.getEmail() : "비공개";
      String phone = member.getPhone() != null ? member.getPhone() : "비공개";
      String myInfo = member.getMyInfo() != null ? member.getMyInfo() : "비공개";

      model.addAttribute("name", name);
      model.addAttribute("email", email);
      model.addAttribute("phone", phone);
      model.addAttribute("myInfo", myInfo);

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

  @PostMapping("/update-name")
  public ResponseEntity<Map<String, Object>> updateName(@RequestBody Map<String, String> request, 
                                                        HttpServletRequest req) {
    String memberId = getMemberIdInJwt(req);
    memberService.updateName(memberId, request.get("name"));
    return ResponseEntity.ok(Map.of("success", true));
  }

  @PostMapping("/update-email")
  public ResponseEntity<Map<String, Object>> updateEmail(@RequestBody Map<String, String> request, 
  HttpServletRequest req) {
    String memberId = getMemberIdInJwt(req);
    memberService.updateEmail(memberId, request.get("email"));
    return ResponseEntity.ok(Map.of("success", true));
  }

  @PostMapping("/update-phone")
  public ResponseEntity<Map<String, Object>> updatePhone(@RequestBody Map<String, String> request, 
  HttpServletRequest req) {
    String memberId = getMemberIdInJwt(req);
    memberService.updatePhone(memberId, request.get("phone"));
    return ResponseEntity.ok(Map.of("success", true));
  }

  @PostMapping("/update-myInfo")
  public ResponseEntity<Map<String, Object>> updateMyInfo(@RequestBody Map<String, String> request, 
  HttpServletRequest req) {
    String memberId = getMemberIdInJwt(req);
    memberService.updateMyInfo(memberId, request.get("myInfo"));
    return ResponseEntity.ok(Map.of("success", true));
  }
}
