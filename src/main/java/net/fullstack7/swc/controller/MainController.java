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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;

    public MainController(CustomOAuth2UserService customOAuth2UserService, JwtTokenProvider jwtTokenProvider) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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

    @GetMapping("/sign/loginSuccess")
    public String loginSuccess(Model model, @AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String memberId = principal.getAttribute("email");
        String token = jwtTokenProvider.createToken(memberId, name, email, null, "google", "Y");

        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return "redirect:/post/main";
    }

    @GetMapping("/sign/loginFailure")
    public String loginFailure(Model model) {
        model.addAttribute("error", "로그인 실패");
        return "/sign/in";
    }
}
