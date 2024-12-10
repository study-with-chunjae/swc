package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.repository.MessageRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/message/send")
@RequiredArgsConstructor
@Log4j2
public class SendMessageController {
    private final MessageService messageService;
    private final MemberServiceIf memberService;
    private final CookieUtil cookieUtil;
    private final MessageRepository messageRepository;

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String handleGetRequest() {
        return "redirect:/message/send/list"; // 삭제 페이지로 직접 GET 요청하면 리다이렉트
    }

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
    //보낸쪽지목록
    @GetMapping("/list")
    public String messageList(Model model, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);

        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        List<Message> messageList = messageService.getSenderMessageList(memberId);
        model.addAttribute("messages", messageList);
        return "message/send/list";
    }

    //상세
    @GetMapping("/view")
    public String messageView(@RequestParam(required = false) Long messageId, Model model, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        if(messageId == null) {
            model.addAttribute("errorMessage", "잘못된 접근입니다.");
            return "message/send/list";
        }
        try {
            Message message = messageService.getMessageById(messageId);
            model.addAttribute("message", message);
            return "message/send/view";
        } catch (Exception e){
            model.addAttribute("errorMessage", "잘못된 접근입니다.");
            return "message/send/list";
        }
    }

    //삭제
    @PostMapping("/delete")
    public String messageDelete(@RequestParam(value = "messageIds", required = false) List<Long> messageIds, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        if(messageIds == null || messageIds.isEmpty()){
            return "redirect:/message/send/list";
        }
        messageService.deleteMessages(messageIds);
        return "redirect:/message/send/list";
    }
}
