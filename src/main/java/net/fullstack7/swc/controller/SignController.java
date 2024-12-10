package net.fullstack7.swc.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.service.CustomOAuth2UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/sign")
public class SignController {
  private final CustomOAuth2UserService customOAuth2UserService;
  private final JwtTokenProvider jwtTokenProvider;

  public SignController(CustomOAuth2UserService customOAuth2UserService, JwtTokenProvider jwtTokenProvider) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @GetMapping("/signUp")
  public String signUp() {
    return "sign/signUp";
  }

  @GetMapping("/signIn")
  public String signIn() {
    return "sign/signIn";
  }

  @GetMapping("/forgotPassword")
  public String forgotPassword() {
    return "sign/forgotPassword";
  }

  @GetMapping("/forgotPasswordChange")
  public String forgotPasswordChange() {
    return "sign/forgotPasswordChange";
  }

  @GetMapping("/loginSuccess")
  public String loginSuccess(Model model, @AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) {

    String token = jwtTokenProvider.createToken(
        principal.getAttribute("email"),
        principal.getAttribute("name"),
        principal.getAttribute("email"),
        null,
        "google",
        "Y",
        principal.getAttribute("picture")
    );

    Cookie cookie = new Cookie("accessToken", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24);
    response.addCookie(cookie);

    return "redirect:/post/main";
  }

  @GetMapping("/loginFailure")
  public String loginFailure(Model model) {
    model.addAttribute("error", "로그인 실패");
    return "sign/signIn";
  }

  @GetMapping("/logout")
  public String logout(HttpServletResponse response) {
    // 쿠키 삭제
    Cookie cookie = new Cookie("accessToken", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    return "redirect:/sign/signIn";
  }
}
