package net.fullstack7.swc.service;

import java.util.Map;
import net.fullstack7.swc.dto.MemberDTO;

import java.util.Map;

public interface MemberServiceIf{
    void signUp(MemberDTO memberDTO);
    String signIn(MemberDTO memberDTO);
    Map<String, String> getMemberInfo(String token);
    boolean checkMemberIdDuplicate(String memberId);
    void sendVerificationEmail(String email);
    boolean verifyEmailCode(String email, String code);
}
