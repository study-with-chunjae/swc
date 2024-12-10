package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.repository.MessageRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/message")
@RequiredArgsConstructor
@Log4j2
public class MessageController {
    private final MessageService messageService;
    private final MemberServiceIf memberService;
    private final CookieUtil cookieUtil;
    private final MessageRepository messageRepository;

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


    //목록
//    @GetMapping("/list")
//    public String chatList(Model model, @RequestParam String receiverId) {
//        List<Message> messageList = messageService.getMessageList(receiverId);
//        model.addAttribute("messages", messageList);
//        return "message/list";
//    }
    //받은쪽지목록
    @GetMapping("/list")
    public String messageList(Model model, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);

        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        List<Message> messageList = messageService.getReceiverMessageList(memberId);
        model.addAttribute("messages", messageList);
        return "message/list";
    }

    //상세
//    @GetMapping("/view")
//    public String messageView(@RequestParam Long messageId, Model model) {
//        Message message = messageService.getMessageById(messageId);
//        model.addAttribute("message", message);
//        return "message/view";
//    }

    // 쪽지 작성
    @GetMapping("/regist")
    public String showRegistForm(Model model, HttpServletRequest req) {
        String senderId = getMemberIdInJwt(req);
        log.info("senderId"+senderId);
        if(senderId == null) {
            return "redirect:/sign/signIn";
        }
        model.addAttribute("senderId", senderId);
        return "message/regist";
    }

    // 쪽지 등록
    @PostMapping("/regist")
    public String registMessage(@RequestParam String receiverId, @RequestParam String content, @RequestParam String title, @RequestParam LocalDateTime regDate, HttpServletRequest req, Model model) {
        String senderId = getMemberIdInJwt(req);
        log.info("senderId"+senderId);

        if(senderId == null) {
            return "redirect:/sign/signIn";
        }

        try{
            messageService.sendMessage(senderId, receiverId, content, title, regDate);
            return "redirect:/message/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "받는 사람을 찾을 수 없습니다.");
            return "message/regist";
        }
        catch (Exception e) {
            model.addAttribute("error", "등록에 실패하였습니다.");
            return "message/regist";
        }
    }

    //삭제
    @PostMapping("/delete")
    public String messageDelete(@RequestParam List<Long> messageIds, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        messageService.deleteMessages(messageIds);
        return "redirect:/message/list";
    }

    //안읽음처리
    @PostMapping("/markAsUnRead")
    @Transactional
    public String markAsUnRead(@RequestParam Long messageId){
        log.info("messageId: "+messageId);
        Message message = messageService.getMessageById(messageId);
        if(message.isRead()){
            message.setRead(false);
            messageRepository.save(message);
            log.info("Message marked as unread: messageId = " + messageId);
        }
        Message updatedMessage = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        log.info("Current message read status: "+message.isRead());

//        return "redirect:/message/view?messageId="+messageId;
        return "redirect:/message/list";
    }

    //상세(누르면 읽음처리까지)
    @GetMapping("/view")
    public String viewMessage(@RequestParam Long messageId, Model model) {
        Message message = messageService.getMessageById(messageId);
        if(message == null){
            throw new IllegalArgumentException("쪽지를 찾을 수 없습니다.");
        }
        if(!message.isRead()){
            message.setRead(true);
            messageRepository.save(message);
        }
        model.addAttribute("message", message);
        return "message/view";
    }
}
