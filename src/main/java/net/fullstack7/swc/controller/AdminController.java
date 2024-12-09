package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/main")
    public String main() {
        return "admin/main";
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "admin/login";
    }

    @GetMapping("/memberList")
    public String memberList() {
        return "admin/memberList";
    }
    @GetMapping("/bbsList")
    public String bbsList() {
        return "admin/bbsList";
    }
    @GetMapping("/qnaList")
    public String qnaList() {
        return "admin/qnaList";
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
