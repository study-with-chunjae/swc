package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.MemberDTO;
import net.fullstack7.swc.service.MemberServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.HashMap;

@Log4j2
@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.signUp(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            log.error("회원가입 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body("회원가입에 실패했습니다.");
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody MemberDTO memberDTO) {
        try {
            String token = memberService.signIn(memberDTO);
            return ResponseEntity.ok(token);
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

    @GetMapping("/check/{memberId}")
    public ResponseEntity<Map<String, Object>> checkMemberIdDuplicate(@PathVariable String memberId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDuplicate = memberService.checkMemberIdDuplicate(memberId);
            response.put("duplicate", isDuplicate);
            response.put("message", isDuplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("아이디 중복 체크 실패: " + e.getMessage());
            response.put("error", "아이디 중복 체크에 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 