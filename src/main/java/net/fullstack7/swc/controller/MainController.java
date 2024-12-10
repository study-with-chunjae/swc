package net.fullstack7.swc.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.service.CustomOAuth2UserService;

@Log4j2
@Controller
public class MainController {
    @GetMapping("/")
    public String main() {
        return "main";
    }
}
