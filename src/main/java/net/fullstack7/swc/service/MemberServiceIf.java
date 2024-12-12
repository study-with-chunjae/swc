package net.fullstack7.swc.service;

import java.util.Map;

import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.dto.MemberDTO;

import java.util.Map;
import net.fullstack7.swc.service.MemberServiceImpl.SignInResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberServiceIf{
    void signUp(MemberDTO memberDTO);
    SignInResponse signIn(MemberDTO memberDTO);
    Map<String, String> getMemberInfo(String token);
    boolean checkMemberIdDuplicate(String memberId);
    void sendVerificationEmail(String email);

    //관리자(수진)
    Page<AdminMemberDTO> getAllMembers(String searchType, String keyword, Pageable pageable);
    int updateMemberStatus();
    int updateStatusByMemberId(String status, String memberId);

    //관리자(수진)
}
