package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Alert;
import net.fullstack7.swc.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    List<Alert> findByMemberAndMsgReadFalseOrderByRegDateDesc(Member member);
    int countByMemberAndMsgReadFalse(Member member);
}