package net.fullstack7.swc.service;

import net.fullstack7.swc.dto.MemberDTO;

public interface MemberServiceIf{
    public void signUp(MemberDTO memberDTO);
    public String signIn(MemberDTO memberDTO);
}
