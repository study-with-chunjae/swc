package net.fullstack7.swc.service;

import net.fullstack7.swc.dto.QnaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QnaServiceIf {
    Integer registQna(QnaDTO qnaDTO); // 글 작성
    QnaDTO viewQna(Integer qnaId, String password, boolean isAdmin); // 글 상세보기
    void addReply(QnaDTO qnaDTO, boolean isAdmin); // 관리자 답변
    void deleteQna(Integer qnaId, String password, boolean isAdmin); // 글 삭제
    List<QnaDTO> listQna();
    QnaDTO adminViewQna(Integer qnaId);

    // 페이징 관련 메서드 추가
    Page<QnaDTO> listQnaPage(Pageable pageable);
    Page<QnaDTO> listQnaByAnsweredPage(Pageable pageable, boolean answered);
}
