package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Message;
import net.fullstack7.swc.dto.MessageDTO;
import net.fullstack7.swc.dto.PageDTO;
import net.fullstack7.swc.repository.MessageRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.MessageService;
import net.fullstack7.swc.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "redirect:/message/send/list";
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
//    //보낸쪽지목록
//    @GetMapping("/list")
//    public String messageList(Model model, HttpServletRequest req) {
//        String memberId = getMemberIdInJwt(req);
//
//        if (memberId == null) {
//            return "redirect:/sign/signIn";
//        }
//        List<Message> messageList = messageService.getSenderMessageList(memberId);
//        model.addAttribute("messages", messageList);
//        return "message/send/list";
//    }
//보낸쪽지목록
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
    pageDTO.initialize("regDate", "desc");

    int totalCount = messageService.getSenderMessageCount(memberId);
    log.info("메시지총개수: {}", totalCount);
    pageDTO.setTotalCount(totalCount);

    List<MessageDTO> messageList = messageService.getSenderMessageList(memberId, pageDTO.getSortPageable());
    log.info("리스트사이즈: {}", messageList.size());
    pageDTO.setDtoList(messageList);

    int totalPages = (int) Math.ceil((double) totalCount / pageDTO.getPageSize());
    model.addAttribute("totalPages", pageDTO.getTotalPage());
    model.addAttribute("currentPage", pageDTO.getPageNo() - 1);
    model.addAttribute("size", pageDTO.getPageSize());
    model.addAttribute("messages", messageList);
    model.addAttribute("pageDTO", pageDTO);
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
