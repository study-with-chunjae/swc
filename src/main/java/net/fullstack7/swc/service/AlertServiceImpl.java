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

    // 알림 등록하는 메서드 이거임
    /*
    String message = receiverId + "님이 친구 요청을 수락했습니다.";
    alertService.registAlert(friend.getRequester(), AlertType.FRIEND_ACCEPTED, message, "/friend/test");
    이런식으로 메세지 쓰고 호출해서 알림받을 사람 아이디, 타입, msg, 이동할 링크 넣기
    */
    @Override
    @Transactional
    public AlertDTO registAlert(Member member, AlertType type, String message, String url) {
        Alert alert = new Alert(member, type, message, url);
        alertRepository.save(alert);

        // 실시간 전송
        messagingTemplate.convertAndSend("/topic/"+member.getMemberId(), new AlertDTO(alert));
        log.info("알림확인 {}: {}", member.getMemberId(), new AlertDTO(alert));

        return new AlertDTO(alert);
    }
    //  미확인 알림리스트
    @Override
    @Transactional(readOnly = true)
    public List<AlertDTO> readAlerts(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        List<Alert> alerts = alertRepository.findByMemberAndMsgReadFalseOrderByRegDateDesc(member);
        return alerts.stream().map(AlertDTO::new).toList();
    }

    //전체리스트
    @Override
    @Transactional(readOnly = true)
    public List<AlertDTO> readAllAlerts(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        List<Alert> alerts = alertRepository.findByMemberOrderByRegDateDesc(member);
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
