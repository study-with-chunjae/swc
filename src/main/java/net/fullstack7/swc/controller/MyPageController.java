package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageController {
  private final MessageService messageService;
  private final MemberServiceIf memberService;
  private final CookieUtil cookieUtil;

  private String getMemberIdInJwt(HttpServletRequest req) {
    String accessToken = cookieUtil.getCookieValue(req, "accessToken");

    if (accessToken == null || accessToken.isEmpty()) {
      return null;
    }

    try {
      return memberService.getMemberInfo(accessToken).get("memberId");
    } catch (Exception e) {
      return null;
    }
  }

  @GetMapping("/myPageInfo")
  public String myPage() {
    return "myPage/myPageInfo";
  }

  @GetMapping("/followList")
  public String myPageFollowList() {
    return "myPage/myPageFollowList";
  }

  @GetMapping("/myPageFriend")
  public String myPageFriend() {
    return "myPage/myPageFriend";
  }

  @GetMapping("/myPageMsg")
  public String myPageMsg() {
    return "myPage/myPageMsg";
  }

  @GetMapping("/myPageSendMsg")
  public String myPageSendMsg(Model model, HttpServletRequest req) {
    String memberId = getMemberIdInJwt(req);

    if (memberId == null) {
      return "redirect:/sign/signIn";
    }
    List<Message> messageList = messageService.getSenderMessageList(memberId);
    model.addAttribute("messages", messageList);

    return "myPage/myPageSendMsg";
  }
}
