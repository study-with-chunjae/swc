package net.fullstack7.swc.domain;

import jakarta.persistence.*;

import lombok.Getter;

@Getter
@Entity
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer qnaId;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)", length = 100)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
    private String question; //질문
    @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
    private String answer; //답변

    // 비회원 작성용 필드
    @Column(nullable = true, length = 100)
    private String email; // 이메일

    @Column(nullable = true, length = 255)
    private String password; // 비밀번호

    @Column(nullable = false)
    private boolean answered;

    // 일단 회원qna 생각없음
//    @ManyToOne
//    @JoinColumn(name = "questionerId")
//    @ToString.Exclude
//    private Member questioner;


    public Qna(String title, String question, String answer, String email) {
        this.title = title;
        this.question = question;
        this.answer = answer;
        this.email = email;
        this.password = password;
        this.answered = false;

    }

    public void addAnswer(String answer) {
        this.answer = answer;
        this.answered = true;
    }
}


