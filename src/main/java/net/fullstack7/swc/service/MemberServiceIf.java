package net.fullstack7.swc.service;

import java.util.Map;
import net.fullstack7.swc.dto.MemberDTO;

import java.util.Map;
import net.fullstack7.swc.service.MemberServiceImpl.SignInResponse;

public interface MemberServiceIf{
    void signUp(MemberDTO memberDTO);
    SignInResponse signIn(MemberDTO memberDTO);
    Map<String, String> getMemberInfo(String token);
    boolean checkMemberIdDuplicate(String memberId);
    void sendVerificationEmail(String email);
}
