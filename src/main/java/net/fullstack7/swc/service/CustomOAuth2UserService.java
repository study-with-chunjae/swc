package net.fullstack7.swc.service;

import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;


import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.config.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 사용자 정보 가져오기
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String memberId = oAuth2User.getAttribute("id");
        String path = oAuth2User.getAttribute("picture");
        log.info("==========================================================================================");
        log.info("OAuth user email : " + email);
        log.info("OAuth user name : " + name);
        log.info("OAuth user memberId : " + email);
        log.info("OAuth user path : " + path);
        log.info("==========================================================================================");

        // DB에 사용자 정보 저장
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .memberId(email)
                            .name(name)
                            .email(email)
                            .social("google")
                            .status("Y")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return memberRepository.save(newMember);
                });

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(member.getMemberId(), member.getName(), member.getEmail(), member.getPhone(), member.getSocial(), member.getStatus());

        log.info("==========================================================================================");
        log.info("token : " + token);
        log.info("==========================================================================================");
        return oAuth2User;
    }
} 