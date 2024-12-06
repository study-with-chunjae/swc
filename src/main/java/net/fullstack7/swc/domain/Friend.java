package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.ToString;

@Getter
@Entity
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer friendId;

    @ManyToOne
    @JoinColumn(name = "receiverId")
    @ToString.Exclude
    private Member receiver; //친구아이디

    @ManyToOne
    @JoinColumn(name = "requesterId")
    @ToString.Exclude
    private Member requester; //친구요청한사람 아이디

    private Integer status; //수락아직안함 0 수락 1
}
