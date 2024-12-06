package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThumbUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thumbUpId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member; //누른사람

    @ManyToOne
    @JoinColumn(name = "postId")
    @ToString.Exclude
    private Post post; //좋아요 받은 게시글
}
