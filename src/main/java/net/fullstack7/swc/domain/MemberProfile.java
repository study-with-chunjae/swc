package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId; // 파일 인덱스
    private String fileName; // 원본파일이름
    private String path; // 파일경로

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member; // Member와의 관계
}
