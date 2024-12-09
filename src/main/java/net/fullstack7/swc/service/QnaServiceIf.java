package net.fullstack7.swc.service;

import net.fullstack7.swc.dto.QnaDTO;

public interface QnaServiceIf {
    Integer registQna(QnaDTO qnaDTO); // 글 작성
    QnaDTO viewQna(Integer qnaId, String password, boolean isAdmin); // 글 상세보기
    void answerQna(QnaDTO qnaDTO, boolean isAdmin); // 관리자 답변
    void deleteQna(Integer qnaId, String password, boolean isAdmin); // 글 삭제
}
