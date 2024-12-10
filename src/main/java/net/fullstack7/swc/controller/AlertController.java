package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import net.fullstack7.swc.domain.AlertType;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AlertDTO;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.service.AlertServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertServiceIf alertService;

    @GetMapping("/list")
    public List<AlertDTO> getAllAlerts(@RequestParam String memberId) {
        return alertService.readAllAlerts(memberId);
    }

    @PostMapping("/readCheck")
    @ResponseBody
    public String readAlert(@RequestParam Integer alertId, @RequestParam String memberId) {
        alertService.checkRead(alertId, memberId);
        return "ok";
    }

    @GetMapping("/unreadCount")
    public int getUnreadCount(@RequestParam String memberId) {
        return alertService.unreadCount(memberId);
    }

    @PostMapping("/allRead")
    public String markAllAsRead(@RequestParam String memberId) {
        alertService.allCheckRead(memberId);
        return "ok";
    }
}
