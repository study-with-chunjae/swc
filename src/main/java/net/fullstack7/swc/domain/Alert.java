package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Entity
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean msgRead = false;

    @Column(nullable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    public Alert() {}

    public Alert(Member member, AlertType type, String message, String url) {
        this.member = member;
        this.type = type;
        this.message = message;
        this.url = url;
    }

    public void checkRead() {
        this.msgRead = true;
    }
}
