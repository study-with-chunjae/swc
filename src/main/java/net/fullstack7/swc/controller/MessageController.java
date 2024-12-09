package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/message")
@RequiredArgsConstructor
@Log4j2
public class MessageController {
    private final MessageService messageService;
    private final MemberServiceIf memberService;
    private final CookieUtil cookieUtil;

    // getMemberIdInJwt 메서드를 MessageController 내에 정의
    private String getMemberIdInJwt(HttpServletRequest req) {
        // 쿠키에서 accessToken을 가져오기
        String accessToken = cookieUtil.getCookieValue(req, "accessToken"); // CookieUtil을 통해 accessToken 가져오기

        if (accessToken == null || accessToken.isEmpty()) {
            return null;  // 토큰이 없으면 null 반환
        }

        // JWT 토큰을 통해 memberId 추출
        try {
            return memberService.getMemberInfo(accessToken).get("memberId");
        } catch (Exception e) {
            // JWT 파싱 오류나 토큰 유효성 검사에서 예외가 발생하면 null 반환
            return null;
        }
    }


    //목록
//    @GetMapping("/list")
//    public String chatList(Model model, @RequestParam String receiverId) {
//        List<Message> messageList = messageService.getMessageList(receiverId);  // 수신자 ID를 통해 목록 조회
//        model.addAttribute("messages", messageList);  // 메시지 목록을 모델에 추가
//        return "message/list";  // 목록을 출력할 Thymeleaf 템플릿
//    }
    @GetMapping("/list")
    public String messageList(Model model, @RequestParam(required = false) String receiverId, HttpServletRequest req) {
        // 로그인 상태 확인
        String memberId = getMemberIdInJwt(req);

//        if (memberId == null) {
//            // 로그인하지 않은 사용자에게는 로그인 페이지로 리다이렉트
//            return "redirect:/";
//        }
//
//        if (receiverId == null || !receiverId.equals(memberId)) {
//            // 로그인된 사용자와 일치하지 않으면 권한을 거부하거나, 다른 처리를 할 수 있습니다.
//            return "errorPage"; // 예시로 에러 페이지를 표시
//        }

        // 로그인된 사용자에게 쪽지 목록 보여주기
        List<Message> messageList = messageService.getMessageList(receiverId);  // 수신자 ID를 통해 목록 조회
        model.addAttribute("messages", messageList);  // 메시지 목록을 모델에 추가
        return "message/list";  // 목록을 출력할 Thymeleaf 템플릿
    }

    //상세
    @GetMapping("/view")
    public String messageView(@RequestParam Long messageId, Model model) {
        Message message = messageService.getMessageById(messageId);  // 메시지 ID로 해당 메시지 조회
        model.addAttribute("message", message);  // 메시지 세부 정보를 모델에 추가
        return "message/view";  // 상세보기 페이지
    }
    // 쪽지 작성 폼 (GET)
    @GetMapping("/regist")
    public String showRegistForm() {
        return "message/regist";  // 위에서 만든 쪽지 작성 폼을 반환
    }

    // 쪽지 등록 처리 (POST)
    @PostMapping("/regist")
    public String registMessage(@RequestParam String senderId, @RequestParam String receiverId, @RequestParam String content) {
        // 쪽지 등록 서비스 호출
        messageService.sendMessage(senderId, receiverId, content);

        // 쪽지 작성 후 목록으로 리다이렉트
        return "redirect:/message/list";  // 쪽지 목록으로 리다이렉트
    }

    //삭제
    @PostMapping("/delete")
    public String messageDelete(@RequestParam List<Long> messageIds, HttpServletRequest req) {
//        String memberId = getMemberIdInJwt(req);
//        if (memberId == null) {
//            return "redirect:/";  // 로그인 안 됐을 경우
//        }
        // 선택된 쪽지 ID들로 삭제 처리
        messageService.deleteMessages(messageIds);
        return "redirect:/message/list";  // 삭제 후 쪽지 목록으로 리다이렉트
    }
}
