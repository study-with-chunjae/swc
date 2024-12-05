package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;  
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
@Getter
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatRoomId;

    @ManyToOne
    @JoinColumn(name = "senderId")
    private Member sender; // 보낸사람

    @ManyToOne
    @JoinColumn(name = "receiverId")
    private Member receiver; // 받는사람

    private Integer senderStatus; // 보낸사람 나감여부 0:나감 1:안나감
    private Integer receiverStatus; // 받은사람 나감여부 0:나감 1:안나감
}
