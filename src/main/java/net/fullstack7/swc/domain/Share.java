package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shareId;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member; //공유받은사람아이디

    @ManyToOne
    @JoinColumn(name = "postId")
    @ToString.Exclude
    private Post post; //게시글 인덱스
}
