package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaPageController {

    private final QnaServiceIf qnaService;

    // QnA 작성 페이지 이동
    @GetMapping("/regist")
    public String createQnaPage() {
        return "qna/regist";
    }

    // QnA 상세보기 페이지 이동
    @GetMapping("/view/{qnaId}")
    public String viewQnaPage(@PathVariable Integer qnaId, Model model) {
        QnaDTO qnaDTO = qnaService.viewQna(qnaId, null, false); // 비회원으로 간주
        model.addAttribute("qna", qnaDTO);
        return "qna/view";
    }

    // QnA 관리 답변 페이지 이동
    @GetMapping("/answer/{qnaId}")
    public String answerQnaPage(@PathVariable Integer qnaId, Model model) {
        model.addAttribute("qnaId", qnaId); // qnaId 전달
        return "qna/answer";
    }

    // QnA 리스트 페이지 이동
    @GetMapping("/list")
    public String listQna(Model model) {
        List<QnaDTO> qnaList = qnaService.listQna();
        model.addAttribute("qnaList", qnaList);
        return "qna/list";
    }
}
