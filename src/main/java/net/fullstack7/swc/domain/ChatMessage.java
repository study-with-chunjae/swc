package net.fullstack7.swc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatMessageId;
    private String message;
    private LocalDateTime createdAt;
    private Integer isRead;//읽음여부 안읽음0 읽음1

    @ManyToOne
    @JoinColumn(name = "chatRoomId")
    @ToString.Exclude
    private ChatRoom chatRoom; //채팅방

    @ManyToOne
    @JoinColumn(name = "senderId")
    @ToString.Exclude
    private Member sender; //보낸사람

    public ChatMessage(ChatRoom chatRoom, Member sender, String message, LocalDateTime createdAt, Integer isRead) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public ChatMessage() {

    }
}
