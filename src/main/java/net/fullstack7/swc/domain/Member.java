package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    public Member(String memberId){
        this.memberId = memberId;
    }
    @Id
    @Column(name = "memberId", nullable = false, length = 50)//회원 id
    private String memberId;
    @Column(name = "pwd", nullable = false, length = 300)//비밀번호
    private String pwd;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "email", nullable = false, length = 100, unique = true)//이메일 (고유)
    private String email;
    @Column(name = "phone", nullable = false, length = 11, columnDefinition = "CHAR(11)")//핸드폰 번호
    private String phone;
    @Column(name="status", nullable = false, length = 1, columnDefinition = "CHAR(1) default 'Y'")
    private String status; // 맴버 상태 <- 기본값 Y : 활성화, N : 비활성화(관리자 비활성화 포함), O : 6개월 이상 로그인 이력X, P : 5회 이상 로그인 실패 비활성화, D : 탈퇴
    private String social;
    private LocalDateTime createdAt; //회원 생성일
    private LocalDateTime updatedAt; //회원 수정일
    private LocalDateTime deletedAt; //회원삭제일
    private LocalDateTime lastLoginAt; //마지막 로그인한 날
    @OneToOne(mappedBy = "member")
    @ToString.Exclude
    private MemberProfile memberProfile;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<MemberRole> roleSet = new HashSet<>();

    public void modifyPassword(String newPwd) {
        this.pwd = newPwd;
    }
    public void modifyEmail(String newEmail) {
        this.email = newEmail;
    }
    public void addRole(MemberRole role) {
        this.roleSet.add(role);
    }
    public void clearRoles(){
        this.roleSet.clear();
    }
}
