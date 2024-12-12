package net.fullstack7.swc.domain;

import jakarta.persistence.*;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer qnaId;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)", length = 100)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
    private String content; //내용

    // 비회원 작성용 필드
    @Column(nullable = true, length = 100)
    private String email; // 이메일

    @Column(nullable = true, length = 255)
    private String password; // 비밀번호

    @Column(nullable = false)
    private boolean answered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Qna parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Qna> replies = new ArrayList<>();

    private LocalDateTime regDate;

    // 일단 회원qna 생각없음
//    @ManyToOne
//    @JoinColumn(name = "questionerId")
//    @ToString.Exclude
//    private Member questioner;


    public Qna(String title, String content, String email, String password, LocalDateTime regDate) {
        this.title = title;
        this.content = content;
        this.email = email;
        this.password = password;
        this.regDate = LocalDateTime.now();
        this.answered = false;
    }

    public Qna() {

    }
    public void addReply(Qna reply) {
        reply.parent = this;
        this.replies.add(reply);
        this.answered = true;
        this.regDate = LocalDateTime.now();
    }

}