package net.fullstack7.swc.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/myPage")
public class MyPageController {
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

  @GetMapping("/myPageMessage")
  public String myPageMessage() {
    return "myPage/myPageMessage";
  }
}
