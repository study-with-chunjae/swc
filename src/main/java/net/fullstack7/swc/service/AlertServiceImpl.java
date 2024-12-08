package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Alert;
import net.fullstack7.swc.domain.AlertType;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AlertDTO;
import net.fullstack7.swc.repository.AlertRepository;
import net.fullstack7.swc.repository.MemberRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertServiceIf {
    private final SimpMessagingTemplate messagingTemplate;
    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public AlertDTO registAlert(Member member, AlertType type, String message, String url) {
        Alert alert = new Alert(member, type, message, url);
        alertRepository.save(alert);

        // 실시간 전송
        messagingTemplate.convertAndSendToUser(member.getMemberId(), "/queue/alerts", new AlertDTO(alert));

        return new AlertDTO(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertDTO> readAlerts(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        List<Alert> alerts = alertRepository.findByMemberAndMsgReadFalseOrderByRegDateDesc(member);
        return alerts.stream().map(AlertDTO::new).toList();
    }

    @Override
    @Transactional
    public void checkRead(Integer alertId, String memberId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다."));
        if (!alert.getMember().getMemberId().equals(memberId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        alert.checkRead();
        alertRepository.save(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public int unreadCount(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));
        return alertRepository.countByMemberAndMsgReadFalse(member);
    }

    @Override
    @Transactional
    public void allCheckRead(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        List<Alert> unreadAlerts = alertRepository.findByMemberAndMsgReadFalseOrderByRegDateDesc(member);
        for (Alert alert : unreadAlerts) {
            alert.checkRead();
        }
    }
}
