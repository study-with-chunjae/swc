package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaPageController {

    // QnA 작성 페이지 이동
    @GetMapping("/create")
    public String createQnaPage() {
        return "qna/regist";
    }

    // QnA 상세보기 페이지 이동
    @GetMapping("/{qnaId}")
    public String viewQnaPage(@PathVariable Integer qnaId, Model model) {
        model.addAttribute("qnaId", qnaId); // qnaId 전달
        return "qna/view";
    }

    // QnA 관리 답변 페이지 이동
    @GetMapping("/answer/{qnaId}")
    public String answerQnaPage(@PathVariable Integer qnaId, Model model) {
        model.addAttribute("qnaId", qnaId); // qnaId 전달
        return "qna/answer";
    }
}