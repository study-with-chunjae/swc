package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;
    private String fileName; //원본파일이름
    private String path; // 파일경로

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member;
}
