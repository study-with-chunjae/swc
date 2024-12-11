package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AlertDTO;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.service.AlertServiceIf;
import net.fullstack7.swc.service.FriendServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor

public class MyPageController {
  private final FriendServiceIf friendService;
  private final CookieUtil cookieUtil;
  private final MemberServiceIf memberService;
  private final AlertServiceIf alertService;


  @GetMapping("/info")
  public String myPage() {
    return "myPage/myPageInfo";
  }

  @GetMapping("/followList")
  public String myPageFollowList(
          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
          @RequestParam(value = "size", required = false, defaultValue = "5") int size,
          Model model
          , HttpServletRequest request
  ) {

    String memberId = getMemberIdInJwt(request);
    log.info("회원아이디{}",memberId);
    Pageable pageable = PageRequest.of(page, size);
    List<Member> searchResults = friendService.searchFriends(keyword, size, page); // 매개변수 순서 확인
    model.addAttribute("searchResults", searchResults);
    model.addAttribute("keyword", keyword);
    model.addAttribute("page", page);
    model.addAttribute("pageSize", size);

    List<FriendDTO> friendRequests = friendService.getFriendRequests(memberId);
    model.addAttribute("friendRequests", friendRequests);

    List<FriendDTO> friends = friendService.getFriends(memberId);
    model.addAttribute("friends", friends);

    List<AlertDTO> alerts = alertService.readAlerts(memberId);
    model.addAttribute("alerts", alerts);
    model.addAttribute("memberId", memberId);


    int unreadCount = alertService.unreadCount(memberId);
    model.addAttribute("unreadCount", unreadCount);

    return "myPage/myPageFollowList";
  }

  @GetMapping("/friend")
  public String myPageFriend(
          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
          @RequestParam(value = "size", required = false, defaultValue = "5") int size,
          Model model
          , HttpServletRequest request
  ) {
    String memberId = getMemberIdInJwt(request);
    log.info("회원아이디{}",memberId);
    Pageable pageable = PageRequest.of(page, size);
    List<Member> searchResults = friendService.searchFriends(keyword, size, page); // 매개변수 순서 확인
    model.addAttribute("searchResults", searchResults);
    model.addAttribute("keyword", keyword);
    model.addAttribute("page", page);
    model.addAttribute("pageSize", size);

    List<FriendDTO> friendRequests = friendService.getFriendRequests(memberId);
    model.addAttribute("friendRequests", friendRequests);

    List<FriendDTO> friends = friendService.getFriends(memberId);
    model.addAttribute("friends", friends);

    List<AlertDTO> alerts = alertService.readAlerts(memberId);
    model.addAttribute("alerts", alerts);
    model.addAttribute("memberId", memberId);


    int unreadCount = alertService.unreadCount(memberId);
    model.addAttribute("unreadCount", unreadCount);

    return "myPage/myPageFriend";
  }

  @GetMapping("/message")
  public String myPageMessage() {
    return "myPage/myPageMsg";
  }

  private String getMemberIdInJwt(HttpServletRequest req){
    String accessToken = cookieUtil.getCookieValue(req,"accessToken");
    return memberService.getMemberInfo(accessToken).get("memberId");
  }
}
