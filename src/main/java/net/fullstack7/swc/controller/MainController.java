package net.fullstack7.swc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/sign/signUp")
    public String signUp() {
        return "/sign/signUp";
    }

    @GetMapping("/sign/signIn")
    public String signIn() {
        return "/sign/signIn";
    }

    @GetMapping("/sign/forgotPassword")
    public String forgotPassword() {
        return "/sign/forgotPassword";
    }

    @GetMapping("/sign/forgotPasswordChange")
    public String forgotPasswordChange() {
        return "/sign/forgotPasswordChange";
    }
}
