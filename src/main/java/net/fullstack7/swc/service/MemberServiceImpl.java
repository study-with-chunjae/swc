package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.config.JwtTokenProvider;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.dto.MemberDTO;
import net.fullstack7.swc.repository.MemberRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@Service
public class MemberServiceImpl implements MemberServiceIf {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Transactional
    public String signIn(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
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

    @Transactional(readOnly = true)
    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.findById(memberId).isPresent();
    }
}
