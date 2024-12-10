package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.repository.AdminRepository;
import org.springframework.stereotype.Service;
import net.fullstack7.swc.domain.Admin;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminServiceIf{
    private final AdminRepository adminRepository;

    public boolean login(String adminId, String password) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new RuntimeException("관리자 아이디가 존재하지 않습니다."));
        if(!admin.getPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return true;
    }
}
