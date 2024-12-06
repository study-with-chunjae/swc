package net.fullstack7.swc.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer qnaId;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question; //질문
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer; //답변

    @ManyToOne
    @JoinColumn(name = "questionerId")
    @ToString.Exclude
    private Member questioner;
}


