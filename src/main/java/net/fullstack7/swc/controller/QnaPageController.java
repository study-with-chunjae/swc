package net.fullstack7.swc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.QnaServiceIf;
import net.fullstack7.swc.util.ErrorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaPageController {

    private final QnaServiceIf qnaService;
    private final ErrorUtil errorUtil;

    // QnA 작성 페이지 이동
    @GetMapping("/regist")
    public String createQnaPage(Model model) {
        model.addAttribute("qnaDTO", new QnaDTO());
        return "qna/regist";
    }

    // QnA 작성 폼 제출 처리
    @PostMapping("/regist")
    public String registQna( @ModelAttribute @Valid QnaDTO qnaDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,



                            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("qnaDTO", qnaDTO);
//            log.info("오류 확인: {}", bindingResult);
            return errorUtil.redirectWithError("/qna/regist", redirectAttributes, bindingResult);
        }

        qnaService.registQna(qnaDTO);
        return "redirect:/qna/list";
    }

    // QnA 리스트 페이지 이동 (페이징 지원)
    @GetMapping("/list")
    public String listQna(
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size, // 페이지 당 항목 수
            @RequestParam(value = "answered", required = false) Boolean answered, // 필터링 옵션 (선택 사항)
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());

        Page<QnaDTO> qnaPage;

        if (answered != null) {
            qnaPage = qnaService.listQnaByAnsweredPage(pageable, answered);
        } else {
            qnaPage = qnaService.listQnaPage(pageable);
        }

        model.addAttribute("qnaPage", qnaPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("answered", answered);

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

//    // 답변 등록 페이지 이동
//    @GetMapping("/answer/{qnaId}")
//    public String answerQnaPage(@PathVariable Integer qnaId, Model model) {
//        model.addAttribute("qnaId", qnaId);
//        model.addAttribute("qnaDTO", new QnaDTO());
//        return "qna/answer";
//    }
//
//    // **답변 등록 처리 메서드 추가**
//    @PostMapping("/{qnaId}/regist")
//    public String submitAnswer(@PathVariable Integer qnaId,
//                               @ModelAttribute QnaDTO qnaDTO,
//                               Model model) {
//        qnaDTO.setParentId(qnaId); // 부모 QnA ID 설정
//        qnaService.addReply(qnaDTO, true);
//        return "redirect:/qna/view/" + qnaId;
//    }
//
//    @PostMapping("/delete/{qnaId}")
//    public String deleteQna(@PathVariable Integer qnaId,
//                            @RequestParam(required = false) String password,
//                            @RequestParam(defaultValue = "false") boolean isAdmin,
//                            Model model) {
//        try {
//            qnaService.deleteQna(qnaId, password, isAdmin);
//            return "redirect:/qna/list";
//        } catch (IllegalArgumentException e) {
//            return "redirect:/qna/list?error=delete";
//        }
//    }
}
