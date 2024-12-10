package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qna")
@Log4j2
@RequiredArgsConstructor
public class QnaRestController {
    private final QnaServiceIf qnaService;

    // QnA 작성
    @PostMapping("/regist")
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

    // 답글 작성
    @PostMapping("/{qnaId}/reply")
    public void addReply(@PathVariable Integer qnaId, @RequestBody QnaDTO qnaDTO) {
        qnaDTO.setParentId(qnaId); // 부모 ID 설정
        qnaService.addReply(qnaDTO, true); // 관리자 여부를 true로 설정 (실제 환경에서는 인증을 통해 설정)
    }

    // QnA 삭제
    @DeleteMapping("/{qnaId}")
    public void deleteQna(@PathVariable Integer qnaId,
                          @RequestParam(required = false) String password,
                          @RequestParam(defaultValue = "false") boolean isAdmin) {
        qnaService.deleteQna(qnaId, password, isAdmin);
    }
}
