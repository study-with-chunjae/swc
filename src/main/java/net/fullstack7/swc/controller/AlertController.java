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

@Controller
@RequiredArgsConstructor
public class AlertController {

    private final AlertServiceIf alertService;
    private final MemberRepository memberRepository;

    @GetMapping("/alerts")
    public String alertList(@RequestParam String memberId, Model model) {
        List<AlertDTO> alerts = alertService.readAlerts(memberId);
        model.addAttribute("alerts", alerts);
        model.addAttribute("memberId", memberId);
        return "alerts";
    }

    @PostMapping("/alerts/read")
    @ResponseBody
    public String readAlert(@RequestParam Integer alertId, @RequestParam String memberId) {
        alertService.checkRead(alertId, memberId);
        return "ok";
    }

    @GetMapping("/alerts/unreadCount")
    public int getUnreadCount(@RequestParam String memberId) {
        return alertService.unreadCount(memberId);
    }

    @PostMapping("/alerts/markAllAsRead")
    public String markAllAsRead(@RequestParam String memberId) {
        alertService.allCheckRead(memberId);
        return "ok";
    }
}
