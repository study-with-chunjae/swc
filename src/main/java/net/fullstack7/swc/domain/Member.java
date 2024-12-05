package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity {
    @Id
    @Column(name = "memberId", nullable = false, length = 50)//회원 id
    private String memberId;
    @Column(name = "pwd", nullable = false, length = 200)//비밀번호
    private String pwd;
    @Column(name = "email", nullable = false, length = 100, unique = true)//이메일 (고유)
    private String email;
    @Column(name = "phone", nullable = false, length = 11, columnDefinition = "CHAR(11)")//핸드폰 번호
    private String phone;
    private boolean isDeleted;
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
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
    public void modifySocial(boolean newSocial) {
        this.social = newSocial;
    }
}
