package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.AlertType;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.dto.MessageDTO;
import net.fullstack7.swc.dto.PageDTO;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.MessageRepository;
import net.fullstack7.swc.service.AlertServiceImpl;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.print.Pageable;
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
    private final AlertServiceImpl alertService;
    private final MemberRepository memberRepository;

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String handleGetRequest() {
        return "redirect:/message/list"; // 삭제 페이지로 직접 GET 요청하면 리다이렉트
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
    //받은쪽지목록
    @GetMapping("/list")
    public String messageList(Model model, HttpServletRequest req,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @Valid PageDTO<MessageDTO> pageDTO, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        log.info("페이지={}, 사이즈={}", page, size);
        if(bindingResult.hasErrors()) {
            pageDTO = PageDTO.<MessageDTO>builder().build();
        }
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        // 페이지 번호가 0인 경우 처리
        if (page < 1) {
            log.warn("유효번호: {}", page);
            page = 1; // 기본값으로 설정
        }

        log.info("페이지={}, 사이즈={}", page, size);

        pageDTO.setPageNo(page);
        pageDTO.setPageSize(size);

//        pageDTO.setSortDirection("desc");
//        pageDTO.setSortField("regDate");

        pageDTO.initialize("regDate", "desc");

        int totalCount = messageService.getReceiverMessageCount(memberId);
        log.info("메시지총개수: {}", totalCount);
        pageDTO.setTotalCount(totalCount);

        List<MessageDTO> messageList = messageService.getReceiverMessageList(memberId, pageDTO.getSortPageable());
        log.info("리스트사이즈: {}", messageList.size());
        pageDTO.setDtoList(messageList);

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageDTO.getPageSize());
        model.addAttribute("totalPages", pageDTO.getTotalPage());
        model.addAttribute("currentPage", pageDTO.getPageNo() - 1); // 0-based index
        model.addAttribute("size", pageDTO.getPageSize());
        model.addAttribute("messages", messageList);
        model.addAttribute("pageDTO", pageDTO);

        return "message/list";
    }

    // 쪽지 작성
    @GetMapping("/regist")
    public String showRegistForm(@RequestParam(required = false) String receiverId, Model model, HttpServletRequest req) {
        String senderId = getMemberIdInJwt(req);
//        log.info("senderId" + senderId);
        if (senderId == null) {
            return "redirect:/sign/signIn";
        }
        if(receiverId == null || receiverId.isEmpty()) {
            receiverId = "";
        }

        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        return "message/regist";
    }

    // 쪽지 등록
    @PostMapping("/regist")
    public String registMessage(@RequestParam String receiverId, @RequestParam String content, @RequestParam String title, @RequestParam LocalDateTime regDate, HttpServletRequest req, Model model) {
        String senderId = getMemberIdInJwt(req);
//        log.info("senderId" + senderId);

        if (senderId == null) {
            return "redirect:/sign/signIn";
        }
        try {
            messageService.sendMessage(senderId, receiverId, content, title, regDate);
            //알림
            String alertMessage = senderId + "님이 새 쪽지를 보냈습니다: " + "'"+title+"'";
            Member member = memberRepository.findByMemberId(receiverId);

            if (member == null || senderId.equals(member.getMemberId())) {
//                log.warn("회원이 존재하지 않습니다. ID: {}", receiverId);
                model.addAttribute("error", "존재하지 않는 회원입니다.");
                return "message/regist";
            }
            alertService.registAlert(member, AlertType.CHAT_MESSAGE, alertMessage, "/message/list");
//            log.info("알림을 보내는 사람: {}, 알림 메시지: {}", senderId, alertMessage);
            return "redirect:/message/send/list";
        } catch (IllegalArgumentException e) {
                model.addAttribute("errorReceiverId", true);
                return "message/regist";
        } catch (Exception e) {
            model.addAttribute("error", true);
            return "message/regist";
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
            return "redirect:/message/list";
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
    public String viewMessage(@RequestParam(required = false) Long messageId, Model model, HttpServletRequest req) {
        String memberId = getMemberIdInJwt(req);
        if (memberId == null) {
            return "redirect:/sign/signIn";
        }
        if(messageId == null) {
            model.addAttribute("errorMessage", "잘못된 접근입니다.");
            return "message/send/list";
        }
        Message message = messageService.getMessageById(messageId);
        if(message == null){
            model.addAttribute("errorMessage", "쪽지를 찾을 수 없습니다.");
            return "message/list";
        }
        if(!message.isRead()){
            message.setRead(true);
            messageRepository.save(message);
        }
        model.addAttribute("message", message);
        return "message/view";
    }
}
