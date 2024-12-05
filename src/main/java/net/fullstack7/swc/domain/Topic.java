package net.fullstack7.swc.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int topicId;
    private String topic;
    @ManyToOne
    @JoinColumn(name = "postId") // Foreign Key
    private Post post;
}
