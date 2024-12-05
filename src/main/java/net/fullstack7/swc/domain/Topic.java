package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int topicId;
    private String topic;
    @ManyToOne
    @JoinColumn(name = "postId") // Foreign Key
    @ToString.Exclude
    private Post post;
}
