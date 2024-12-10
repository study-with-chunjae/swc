package net.fullstack7.swc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@ToString
public class AdminMemberDTO {
    private String memberId;
    private String pwd;
    private String name;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt; // 회원 생성일
    private LocalDateTime updatedAt; // 회원 수정일
    private LocalDateTime deletedAt; // 회원 삭제일
    private LocalDateTime lastLoginAt;
}
