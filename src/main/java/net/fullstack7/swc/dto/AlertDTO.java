package net.fullstack7.swc.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Alert;
import net.fullstack7.swc.domain.AlertType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class AlertDTO {
    private Integer alertId;
    private String memberId;
    private AlertType type;
    private String message;
    private String url;
    private boolean msgRead;
    private LocalDateTime regDate;
    private int unreadCount;

    public AlertDTO(Alert alert) {
        this.alertId = alert.getAlertId();
        this.memberId = alert.getMember().getMemberId();
        this.type = alert.getType();
        this.message = alert.getMessage();
        this.url = alert.getUrl();
        this.msgRead = alert.isMsgRead();
        this.regDate = alert.getRegDate();
    }

}
