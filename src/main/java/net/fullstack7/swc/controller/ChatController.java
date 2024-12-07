package net.fullstack7.swc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.ChatRoom;
import net.fullstack7.swc.repository.ChatMessageRepository;
import net.fullstack7.swc.repository.ChatRoomReposotory;
import net.fullstack7.swc.service.ChatMessageServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Log4j2
public class ChatController {
    private final ChatMessageRepository messageRepository;
    private final ChatRoomReposotory roomRepository;

    //채팅목록
    @GetMapping("/list")
    public String chatList(Model model) {
        return "chat/list";
    }
    //채팅상세
    @GetMapping("/view")
    public String chatView(Model model) {
        return "chat/view";
    }
    //채팅생성
    @PostMapping("/regist")
    public String chatRegist(@RequestParam int chatRoomId, Model model) {
        return "chat/view?chatRoomId="+chatRoomId;
    }
    //채팅삭제
    @PostMapping("/delete")
    public String chatDelete(@RequestParam String receiverId, @RequestParam String senderId) {
        return "chat/list";
    }
}
