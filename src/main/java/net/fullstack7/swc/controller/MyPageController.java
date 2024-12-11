package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.dto.FriendListDTO;
import net.fullstack7.swc.dto.PageDTO;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.ErrorUtil;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AlertDTO;
import net.fullstack7.swc.dto.FriendDTO;
import net.fullstack7.swc.service.AlertServiceIf;
import net.fullstack7.swc.service.FriendServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.service.MemberServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageController {
  private final FriendServiceIf friendService;
  private final CookieUtil cookieUtil;
  private final MemberServiceImpl memberService;
  private final AlertServiceIf alertService;
  private final MessageService messageService;
  private final ErrorUtil errorUtil;
  private final View error;

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
          @Valid PageDTO<FriendListDTO> pageDTO,
          BindingResult bindingResult,
          RedirectAttributes redirectAttributes,
          Model model,
          HttpServletRequest request
          ) {
    LogUtil.logLine("MypageController friendList");
    if(bindingResult.hasErrors()) {
      return errorUtil.redirectWithError("/post/main",redirectAttributes,bindingResult);
    }
    try {
      String memberId = getMemberIdInJwt(request);
      LogUtil.log("memberId",memberId);
      pageDTO.setPageSize(8);
      pageDTO.initialize("regDate","desc");
      pageDTO.setTotalCount(friendService.getTotalCount(pageDTO,memberId));
      pageDTO = friendService.getFriendList(pageDTO,memberId);
      model.addAttribute("pageDTO", pageDTO);
      return "myPage/myPageFriend";
    }catch(Exception e) {
      return errorUtil.redirectWithError(e.getMessage(), "/post/main", redirectAttributes);
    }
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
