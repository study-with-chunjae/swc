package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.dto.MemberDTO;
import net.fullstack7.swc.service.MemberServiceImpl;
import net.fullstack7.swc.service.MemberServiceImpl.SignInResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.HashMap;

@Log4j2
@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.signUp(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            log.error("회원가입 실패 - memberId: {}, 원인: {}", memberDTO.getMemberId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody MemberDTO memberDTO) {
        try {
            SignInResponse response = memberService.signIn(memberDTO);
            return ResponseEntity.ok()
                    .header("X-Temp-Password", String.valueOf(response.isTempPassword()))
                    .body(response.getToken());
        } catch (Exception e) {
            log.error("로그인 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/member/info")
    public ResponseEntity<?> getMemberInfo(@RequestHeader("Authorization") String token) {
        try {
            Map<String, String> memberInfo = memberService.getMemberInfo(token);
            return ResponseEntity.ok(memberInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 없어질 예정
    @PostMapping("/cookie-test")
    public ResponseEntity<Map<String, String>> cookieTest(@RequestParam String accessToken) {
        try {
            String memberId = memberService.getMemberInfo(accessToken).get("memberId");
            Map<String, String> memberInfo = memberService.getMemberInfo(accessToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("memberId", memberInfo.get("memberId"));
            response.put("name", memberInfo.get("name"));
            response.put("email", memberInfo.get("email"));
            response.put("phone", memberInfo.get("phone"));
            response.put("social", memberInfo.get("social"));
            response.put("status", memberInfo.get("status"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    // 없어질 예정
    
    @GetMapping("/check/{memberId}")
    public ResponseEntity<Map<String, Object>> checkMemberIdDuplicate(@PathVariable String memberId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDuplicate = memberService.checkMemberIdDuplicate(memberId);
            response.put("duplicate", isDuplicate);
            response.put("message", isDuplicate ? 
                "이미 사용 중인 아이디입니다." : 
                "사용 가능한 아이디입니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("duplicate", true);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            memberService.sendVerificationEmail(email);
            return ResponseEntity.ok().body(Map.of("message", "인증번호가 전송되었습니다."));
        } catch (Exception e) {
            log.error("이메일 인증번호 전송 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            boolean isValid = memberService.verifyEmail(email, code);
            return ResponseEntity.ok().body(Map.of("verified", isValid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            memberService.sendTemporaryPassword(request.get("memberId"));
            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        try {
            // 필요한 검증 로직
            return "/sign/forgotPasswordChange";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/sign/forgotPasswordChange";
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request, 
                                               @CookieValue(name = "accessToken", required = false) String token) {
        try {
            if (token == null) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            String memberId = jwtTokenProvider.getMemberId(token);
            memberService.changePassword(memberId, request.get("newPassword"));
            
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 