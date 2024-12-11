package net.fullstack7.swc.service;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.dto.MemberDTO;
import net.fullstack7.swc.repository.AlertRepository;
import net.fullstack7.swc.repository.FriendRepository;
import net.fullstack7.swc.repository.MemberProfileRepository;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.MessageRepository;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.repository.ShareRepository;
import net.fullstack7.swc.repository.ThumbUpRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    //관리자(수진)
    private final ModelMapper modelMapper;
    //관리자(수진)

    // 모든 memberId 관련 DB 내용 삭제 //
    private final AlertRepository alertRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PostRepository postRepository;
    private final ShareRepository shareRepository;
    private final ThumbUpRepository thumbUpRepository;
    private final FriendRepository friendRepository;
    private final MessageRepository messageRepository;
    // 모든 memberId 관련 DB 내용 삭제 //

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
        // 이메일 중복 체크
        checkEmailDuplicate(memberDTO.getEmail());
        
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

    @Getter
    @Builder
    public static class SignInResponse {
        private String token;
        private boolean tempPassword;
    }

    // 로그인
    @Transactional // 트랜잭션 관리를 위한 어노테이션. 예외 발생 시 롤백. <- JPA 쓸라면 당근 써야할듯
    public SignInResponse signIn(MemberDTO memberDTO) {
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

//        if (!passwordEncoder.matches(memberDTO.getPwd(), member.getPwd())) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
//        }

        member.updateLastLoginAt();
        memberRepository.save(member);

        String token = jwtTokenProvider.createToken(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getSocial(),
                member.getStatus(),
                //member.getMemberProfile().getPath() != null ? member.getMemberProfile().getPath() : null

                null
        );


        log.info("임시 비밀번호 상태: {}", member.isTemporaryPassword());

        return SignInResponse.builder()
                .token(token)
                .tempPassword(member.isTemporaryPassword())
                .build();
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
            if (memberRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }

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
            htmlContent.append("<p>아래의 인증번호를 입력해주세요.</p>");
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

    public boolean verifyEmail(String email, String code) {
        VerificationCode savedVerification = verificationCodes.get(email);
        if (savedVerification == null || !savedVerification.isValid(code)) {
            throw new RuntimeException("인증번호가 만료되었거나 일치하지 않습니다.");
        }
        
        verificationCodes.remove(email);
        return true;
    }

    // 임시 비밀번호 이메일 전송
    @Transactional
    public void sendTemporaryPassword(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 임시 비밀번호 생성 (영문 대소문자, 숫자, 특수문자 포함 12자리)
        String temporaryPassword = generateTemporaryPassword();
        
        // 비밀번호 암호화하여 저장
        member.modifyPassword(passwordEncoder.encode(temporaryPassword));
        member.setTemporaryPassword(true);
        memberRepository.save(member);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(member.getEmail());
            helper.setSubject("[SWC] 임시 비밀번호 안내");
            
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html>");
            htmlContent.append("<html>");
            htmlContent.append("<head>");
            htmlContent.append("<meta charset='UTF-8'>");
            htmlContent.append("<style>");
            htmlContent.append(".container { width: 100%; max-width: 600px; margin: 0 auto; font-family: 'Arial', sans-serif; }");
            htmlContent.append(".header { background-color: #4A90E2; padding: 20px; text-align: center; }");
            htmlContent.append(".content { padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0; }");
            htmlContent.append(".temp-password { font-size: 24px; font-weight: bold; text-align: center; color: #4A90E2; padding: 20px; margin: 20px 0; background-color: #f8f9fa; border-radius: 5px; }");
            htmlContent.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }");
            htmlContent.append("</style>");
            htmlContent.append("</head>");
            htmlContent.append("<body>");
            htmlContent.append("<div class='container'>");
            htmlContent.append("<div class='header'>");
            htmlContent.append("<h1 style='color: white;'>임시 비밀번호 안내</h1>");
            htmlContent.append("</div>");
            htmlContent.append("<div class='content'>");
            htmlContent.append("<p>안녕하세요. SWC 개발팀입니다.</p>");
            htmlContent.append("<p>요청하신 임시 비밀번호를 안내해 드립니다.</p>");
            htmlContent.append("<p>로그인 이후 반드시 비밀번호를 변경해 주세요.</p>");
            htmlContent.append("<div class='temp-password'>");
            htmlContent.append(temporaryPassword);
            htmlContent.append("</div>");
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
            
            log.info("임시 비밀번호 이메일 전송 완료: {}", member.getEmail());
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("임시 비밀번호 이메일 전송 실패: " + e.getMessage());
        }
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // 최소 조건을 만족시키기 위해 각 카테고리에서 하나씩 선택
        password.append(chars.substring(0, 26).charAt(random.nextInt(26))); // 대문자
        password.append(chars.substring(26, 52).charAt(random.nextInt(26))); // 소문자
        password.append(chars.substring(52, 62).charAt(random.nextInt(10))); // 숫자
        password.append(chars.substring(62).charAt(random.nextInt(8))); // 특수문자
        
        // 나머지 8자리는 무작위로 선택
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 문자열 섞기
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }

    @Transactional
    public void changePassword(String memberId, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            
        // 비밀번호 유효성 검사
        if (!isValidPassword(newPassword)) {
            throw new RuntimeException("비밀번호는 영문, 숫자, 특수문자를 포함하여 10~20자리여야 합니다.");
        }
        
        member.modifyPassword(passwordEncoder.encode(newPassword));
        member.setTemporaryPassword(false);  // 임시 비밀번호 상태 해제
        memberRepository.save(member);
    }

    private boolean isValidPassword(String password) {
        // 비밀번호 정규식: 영문, 숫자, 특수문자 포함 10~20자
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,20}$";
        return password.matches(passwordRegex);
    }

    // 이메일 중복 체크 메소드 추가
    private void checkEmailDuplicate(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
    }

    //관리자(수진)
    @Override
    public Page<AdminMemberDTO> getAllMembers(String searchType, String keyword, Pageable pageable) {
        Page<Member> members;

        if (searchType == null || searchType.isBlank()) {
            searchType = "all";
        }
        if (keyword == null) {
            keyword = "";
        }

        switch (searchType) {
            case "memberId":
                members = memberRepository.findByMemberIdContaining(keyword, pageable);
                break;
            case "name":
                members = memberRepository.findByNameContaining(keyword, pageable);
                break;
            case "status":
                members = memberRepository.findByStatus(keyword, pageable);
                break;
            default:
                members = memberRepository.findAll(pageable);
        }

        return members.map(member -> modelMapper.map(member, AdminMemberDTO.class));
    }

// 이게 진짜임
    @Override
    @Transactional
    public int updateMemberStatus(){
        LocalDateTime cutoffDate = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);
        return memberRepository.updateStatusForMembers(cutoffDate);
    }
    // 테스트
//    @Override
//    @Transactional
//    public int updateMemberStatus(){
//        LocalDateTime cutoffDate = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
//        return memberRepository.updateStatusForMembers(cutoffDate);
//    }

    @Transactional
    @Override
    public int updateStatusByMemberId(String status, String memberId) {
        return memberRepository.updateStatusByMemberId(status, memberId);
    }
    //관리자(수진)

    @Transactional
    public void updateName(String memberId, String newName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        member.modifyName(newName);
        memberRepository.save(member);
    }

    @Transactional
    public void updateEmail(String memberId, String newEmail) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        member.modifyEmail(newEmail);
        memberRepository.save(member);
    }

    @Transactional
    public void updatePhone(String memberId, String newPhone) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        member.modifyPhone(newPhone);
        memberRepository.save(member);
    }

    @Transactional
    public void updateMyInfo(String memberId, String newMyInfo) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        member.modifyMyInfo(newMyInfo);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteMember(String memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("실패"));
        member.changeMemberStatus("D");
        memberRepository.save(member);

        alertRepository.deleteAllByMember(member);
        memberProfileRepository.deleteAllByMember(member);
        postRepository.deleteAllByMember(member);
        shareRepository.deleteAllByMember(member);
        thumbUpRepository.deleteAllByMember(member);
        friendRepository.deleteAllByReceiver(member);
        friendRepository.deleteAllByRequester(member);
        messageRepository.deleteAllBySenderIdOrReceiverId(memberId, memberId);
    }
}

