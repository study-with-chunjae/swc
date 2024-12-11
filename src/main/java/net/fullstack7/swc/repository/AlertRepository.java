package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Alert;
import net.fullstack7.swc.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    // 미확인 리스트
    List<Alert> findByMemberAndMsgReadFalseOrderByRegDateDesc(Member member);
    int countByMemberAndMsgReadFalse(Member member);
    // 전체리스트
    List<Alert> findByMemberOrderByRegDateDesc(Member member);

    // 아이디 전체 삭제 (한덕용 추가)
    void deleteAllByMember(Member member);
}