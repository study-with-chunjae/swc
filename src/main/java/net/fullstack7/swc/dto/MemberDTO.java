package net.fullstack7.swc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@ToString
public class MemberDTO {
    @NotBlank(message = "회원 ID는 필수입니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "회원 ID는 영어 소문자와 숫자만 포함할 수 있습니다.")
    private String memberId; // 회원 ID

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,20}$", message = "비밀번호는 영어, 숫자, 특수문자를 포함해야 하며, 10~20자여야 합니다.")
    private String pwd;      // 비밀번호

    @NotBlank(message = "이름은 필수입니다.")
    private String name;     // 이름

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;    // 이메일

    @NotBlank(message = "핸드폰 번호는 필수입니다.")
    @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리 숫자여야 합니다.")
    private String phone;    // 핸드폰 번호

    private String status;   // 회원 상태
    private String social;   // 소셜 정보
    private Integer fileId;  // 프로필 파일 인덱스
    private String fileName; // 프로필 파일 원본 이름
    private String path;     // 프로필 파일 경로
    private LocalDateTime createdAt; // 회원 생성일
    private LocalDateTime updatedAt; // 회원 수정일
    private LocalDateTime deletedAt; // 회원 삭제일
    private LocalDateTime lastLoginAt; // 마지막 로그인한 날
}