package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/message/send")
@RequiredArgsConstructor
@Log4j2
public class SendMessageController {
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
    public String messageView(@RequestParam Long messageId, Model model) {
        Message message = messageService.getMessageById(messageId);
        model.addAttribute("message", message);
        return "message/send/view";
    }

    //삭제
    @PostMapping("/delete")
    public String messageDelete(@RequestParam List<Long> messageIds, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        messageService.deleteMessages(messageIds);
        return "redirect:/message/send/list";
    }
}
