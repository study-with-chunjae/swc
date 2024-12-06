package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.ChatRoom;

public interface ChatRoomServiceIf {
    //채팅방 생성
    public String registChatRoom(ChatRoom chatRoom);
    //나감여부
}
