package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaPageController {

    private final QnaServiceIf qnaService;

    // QnA 작성 페이지 이동
    @GetMapping("/regist")
    public String createQnaPage(Model model) {
        model.addAttribute("qnaDTO", new QnaDTO());
        return "qna/regist";
    }

    // QnA 작성 폼 제출 처리
    @PostMapping("/regist")
    public String registQna(@ModelAttribute QnaDTO qnaDTO, Model model) {
        Integer qnaId = qnaService.registQna(qnaDTO);
        return "redirect:/qna/list";
    }

    // QnA 리스트 페이지 이동
    @GetMapping("/list")
    public String listQna(Model model) {
        model.addAttribute("qnaList", qnaService.listQna());
        return "qna/list";
    }

    @GetMapping("/view/{qnaId}")
    public String viewQnaPageRedirect(@PathVariable Integer qnaId) {
        return "redirect:/qna/list?view=" + qnaId;
    }

    @PostMapping("/view/{qnaId}/password")
    public String verifyPassword(@PathVariable Integer qnaId,
                                 @RequestParam String password,
                                 Model model) {
        try {
            QnaDTO qnaDTO = qnaService.viewQna(qnaId, password, false);
            model.addAttribute("qna", qnaDTO);
            return "qna/view";
        } catch (IllegalArgumentException e) {
            return "redirect:/qna/list?view=" + qnaId + "&error=password";
        }
    }

    // QnA 상세보기 페이지 이동
    @GetMapping("/view/{qnaId}/detail")
    public String viewQnaPageDetail(@PathVariable Integer qnaId, Model model) {
        QnaDTO qnaDTO = qnaService.viewQna(qnaId, null, false);
        model.addAttribute("qna", qnaDTO);
        return "qna/view";
    }

    // 답변 등록 페이지 이동
    @GetMapping("/answer/{qnaId}")
    public String answerQnaPage(@PathVariable Integer qnaId, Model model) {
        model.addAttribute("qnaId", qnaId);
        model.addAttribute("qnaDTO", new QnaDTO());
        return "qna/answer";
    }

    // **답변 등록 처리 메서드 추가**
    @PostMapping("/{qnaId}/regist")
    public String submitAnswer(@PathVariable Integer qnaId,
                               @ModelAttribute QnaDTO qnaDTO,
                               Model model) {
        qnaDTO.setParentId(qnaId); // 부모 QnA ID 설정
        qnaService.addReply(qnaDTO, true); // 관리자 답변이라고 가정 (실제 환경에서는 인증을 통해 설정)
        return "redirect:/qna/view/" + qnaId;
    }

    @PostMapping("/delete/{qnaId}")
    public String deleteQna(@PathVariable Integer qnaId,
                            @RequestParam(required = false) String password,
                            @RequestParam(defaultValue = "false") boolean isAdmin,
                            Model model) {
        try {
            qnaService.deleteQna(qnaId, password, isAdmin);
            return "redirect:/qna/list";
        } catch (IllegalArgumentException e) {
            return "redirect:/qna/list?error=delete";
        }
    }
}
