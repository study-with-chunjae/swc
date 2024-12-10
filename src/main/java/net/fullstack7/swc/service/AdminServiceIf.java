package net.fullstack7.swc.service;


import net.fullstack7.swc.dto.AdminDTO;

public interface AdminServiceIf {
    public boolean login(String adminId, String password);
}
