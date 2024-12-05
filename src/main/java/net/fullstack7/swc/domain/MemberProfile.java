package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;
    private String fileName; //원본파일이름
    private String path; // 파일경로

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;
}
