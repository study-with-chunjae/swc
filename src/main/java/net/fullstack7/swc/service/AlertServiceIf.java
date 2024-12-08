package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.AlertType;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AlertDTO;

import java.util.List;

public interface AlertServiceIf {
    AlertDTO registAlert(Member member, AlertType type, String message, String url);
    List<AlertDTO> readAlerts(String memberId);
    void checkRead(Integer alertId, String memberId);
    int unreadCount(String memberId);
    void allCheckRead(String memberId);
}
