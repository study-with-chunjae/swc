package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import net.fullstack7.swc.domain.Qna;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.repository.QnaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaServiceImpl implements QnaServiceIf {

    private final QnaRepository qnaRepository;
    private final JavaMailSender mailSender; // 의존성 주입 필요

    @Override
    public Integer registQna(QnaDTO qnaDTO) {
        Qna qna = new Qna(
                qnaDTO.getTitle(),
                qnaDTO.getQuestion(),
                qnaDTO.getEmail(),
                qnaDTO.getPassword()
        );

        Qna savedQna = qnaRepository.save(qna);
        return savedQna.getQnaId();
    }

    @Override
    @Transactional(readOnly = true)
    public QnaDTO viewQna(Integer qnaId, String password, boolean isAdmin) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA가 존재하지 않습니다."));

        if (!isAdmin) {
            if (qna.getPassword() != null && !qna.getPassword().equals(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }

        return QnaDTO.builder()
                .qnaId(qna.getQnaId())
                .title(qna.getTitle())
                .question(qna.getQuestion())
                .answer(qna.getAnswer())
                .answered(qna.isAnswered())
                .email(qna.getEmail())
                .build();
    }

    @Override
    public void answerQna(QnaDTO qnaDTO, boolean isAdmin) {
        if(!isAdmin) {
            throw new SecurityException("관리자만 답변할 수 있습니다.");
        }

        Qna qna = qnaRepository.findById(qnaDTO.getQnaId())
                .orElseThrow(() -> new IllegalArgumentException("QnA가 존재하지 않습니다."));
        qna.addAnswer(qnaDTO.getAnswer());

        // 이메일 발송
        if (qna.getEmail() != null && !qna.getEmail().isEmpty()) {
            sendMail(qna.getEmail(), qna.getTitle(), qnaDTO.getAnswer());
        }
    }

    @Override
    public void deleteQna(Integer qnaId, String password, boolean isAdmin) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("QnA가 존재하지 않습니다."));

        if(!isAdmin) {
            // 비밀번호 검증
            if (qna.getPassword() == null || !qna.getPassword().equals(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        }

        qnaRepository.delete(qna);
    }

    private void sendMail(String toEmail, String subject, String answerContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("QnA 답변: " + subject);
        message.setText("답변 내용:\n" + answerContent);
        mailSender.send(message);
    }
}
