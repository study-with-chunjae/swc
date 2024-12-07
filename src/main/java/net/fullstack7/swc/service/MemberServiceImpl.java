package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.MemberDTO;
import net.fullstack7.swc.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Log4j2
@Service
public class MemberServiceImpl implements MemberServiceIf {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static class VerificationCode {
        String code;
        LocalDateTime expiryTime;

        VerificationCode(String code) {
            this.code = code;
            this.expiryTime = LocalDateTime.now().plusMinutes(5);
        }

        boolean isValid(String inputCode) {
            return code.equals(inputCode) && LocalDateTime.now().isBefore(expiryTime);
        }
    }

    // 회원가입
    @Transactional
    public void signUp(MemberDTO memberDTO) {
        Member member = Member.builder()
                .memberId(memberDTO.getMemberId())
                .pwd(passwordEncoder.encode(memberDTO.getPwd()))
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .phone(memberDTO.getPhone())
                .status("Y")
                .social(memberDTO.getSocial())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    // 로그인
    @Transactional // 트랜잭션 관리를 위한 어노테이션. 예외 발생 시 롤백. <- JPA 쓸라면 당근 써야할듯
    public String signIn(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 상태 체크
        switch (member.getStatus()) {
            case "N":
                throw new RuntimeException("관리자 또는 이용 규칙 위반에 의해 이용이 제한된 아이디입니다.\n관리자에게 문의해 주세요.");
            case "O":
                throw new RuntimeException("6개월 이상 로그인 이력이 없습니다.\n관리자에게 문의해 주세요.");
            case "P":
                throw new RuntimeException("5회 이상 로그인 실패로 잠금 처리된 아이디입니다.\n관리자에게 문의해 주세요.");
            case "Y":
                break;
            default:
                throw new RuntimeException("잘못된 계정 상태입니다.");
        }

        if (!passwordEncoder.matches(memberDTO.getPwd(), member.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        member.updateLastLoginAt();
        memberRepository.save(member);

        return jwtTokenProvider.createToken(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getSocial(),
                member.getStatus()
        );
    }

    // 토큰 검증 후 회원 정보 반환
    @Transactional(readOnly = true)
    public Map<String, String> getMemberInfo(String token) {
        // 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("만료됨");
        }

        // 이름 그대로 저장함
        Map<String, String> response = new HashMap<>();
        response.put("memberId", jwtTokenProvider.getMemberId(token));
        response.put("name", jwtTokenProvider.getName(token));
        response.put("email", jwtTokenProvider.getEmail(token));
        response.put("phone", jwtTokenProvider.getPhone(token));
        response.put("social", jwtTokenProvider.getSocial(token));
        response.put("status", jwtTokenProvider.getStatus(token));
        return response;
    }

    // 아이디 중복 체크
    @Transactional(readOnly = true)
    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.findById(memberId).isPresent();
    }

    // 이메일 인증 (예쁘게 보내기)
    public void sendVerificationEmail(String email) {
        try {
            String code = String.format("%06d", new Random().nextInt(1000000));
            verificationCodes.put(email, new VerificationCode(code));

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[SWC] 인증번호 안내메일");
            
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html>");
            htmlContent.append("<html>");
            htmlContent.append("<head>");
            htmlContent.append("<meta charset='UTF-8'>");
            htmlContent.append("<style>");
            htmlContent.append(".container { width: 100%; max-width: 600px; margin: 0 auto; font-family: 'Arial', sans-serif; }");
            htmlContent.append(".header { background-color: #4A90E2; padding: 20px; text-align: center; }");
            htmlContent.append(".content { padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; }");
            htmlContent.append(".verification-code { font-size: 32px; font-weight: bold; text-align: center; color: #4A90E2; padding: 20px; margin: 20px 0; background-color: #f8f9fa; border-radius: 5px; }");
            htmlContent.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }");
            htmlContent.append("</style>");
            htmlContent.append("</head>");
            htmlContent.append("<body>");
            htmlContent.append("<div class='container'>");
            htmlContent.append("<div class='header'>");
            htmlContent.append("<h1 style='color: white;'>SWC 이메일 인증</h1>");
            htmlContent.append("</div>");
            htmlContent.append("<div class='content'>");
            // 이미지 넣을지 말지 고민중
            htmlContent.append("<p>안녕하세요. SWC 개발팀입니다.</p>");
            htmlContent.append("<p>SWC 회원가입을 위한 인증번호입니다.</p>");
            htmlContent.append("<p>아래의 인증번호를 입���해주세요.</p>");
            htmlContent.append("<div class='verification-code'>");
            htmlContent.append(code);
            htmlContent.append("</div>");
            htmlContent.append("<p>이 인증번호는 5분 동안만 유효합니다.</p>");
            htmlContent.append("</div>");
            htmlContent.append("<div class='footer'>");
            htmlContent.append("<p>본 메일은 발신전용 메일이므로 회신이 되지 않습니다.</p>");
            htmlContent.append("<p>© 2024 SWC. All rights reserved.</p>");
            htmlContent.append("</div>");
            htmlContent.append("</div>");
            htmlContent.append("</body>");
            htmlContent.append("</html>");

            helper.setText(htmlContent.toString(), true);
            mailSender.send(mimeMessage);
            
            log.info("이메일 전송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    public boolean verifyEmailCode(String email, String code) {
        VerificationCode storedCode = verificationCodes.get(email);
        if (storedCode != null && storedCode.isValid(code)) {
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }
}
