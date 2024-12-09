package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaRestController {
    private final QnaServiceIf qnaService;

    // QnA 작성
    @PostMapping
    public Integer registQna(@RequestBody QnaDTO qnaDTO) {
        return qnaService.registQna(qnaDTO);
    }

    // QnA 상세보기
    @GetMapping("/{qnaId}")
    public QnaDTO viewQna(@PathVariable Integer qnaId,
                          @RequestParam(required = false) String password,
                          @RequestParam(defaultValue = "false") boolean isAdmin) {
        return qnaService.viewQna(qnaId, password, isAdmin);
    }

    // 관리자가 답변 등록
    @PostMapping("/answer")
    public void answerQna(@RequestBody QnaDTO qnaDTO,
                          @RequestParam(defaultValue = "false") boolean isAdmin) {
        qnaService.answerQna(qnaDTO, isAdmin);
    }

    // QnA 삭제
    @DeleteMapping("/{qnaId}")
    public void deleteQna(@PathVariable Integer qnaId,
                          @RequestParam(required = false) String password,
                          @RequestParam(defaultValue = "false") boolean isAdmin) {
        qnaService.deleteQna(qnaId, password, isAdmin);
    }
}
