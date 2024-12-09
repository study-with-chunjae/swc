package net.fullstack7.swc.service;


import net.fullstack7.swc.domain.ChatMessage;
import net.fullstack7.swc.domain.ChatRoom;

public interface ChatMessageServiceIf {
    void sendMessage(ChatMessage message, ChatRoom room);

}
