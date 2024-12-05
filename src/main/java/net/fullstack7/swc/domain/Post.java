package net.fullstack7.swc.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    private Integer todayType; //오늘의 학습노출여부(0:비공개 1:공개)
    private LocalDateTime displayAt; //오늘의 학습노출시작일
    private LocalDateTime displayEnd; //오늘의 학습노출시작일
    private LocalDateTime createdAt; //게시글 생성일
    @OneToMany
    @ToString.Exclude
    private Set<Topic> topics; //분야
    private String hashtag; //해시태그

    @ManyToOne
    @JoinColumn(name = "userId")
    @ToString.Exclude
    private Member member;

    @OneToMany
    @ToString.Exclude
    private List<ThumbUp> thumbUps;
}
