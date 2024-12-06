package net.fullstack7.swc.service;

import net.fullstack7.swc.dto.MemberDTO;

import java.util.Map;

public interface MemberServiceIf{
    public void signUp(MemberDTO memberDTO);
    public String signIn(MemberDTO memberDTO);

    //강감찬 Impl에 잇는데 Interface 에 없는거 추가함
    public Map<String, String> getMemberInfo(String token);
    public boolean checkMemberIdDuplicate(String memberId);
    //강감찬

}
