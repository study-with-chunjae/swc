package net.fullstack7.swc.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dummy")
public class DummyController {

    @GetMapping("/toast")
    public String toast() {
        return "post/test";
    }
  
    @GetMapping("/main/main")
    public String main() {
        return "main/main";
    }

    @GetMapping("/main/main2")
    public String I() {
        return "main/main2";
    }

    @GetMapping("/main/layout")
    public String layout() {
        return "main/layout";
    }

    @GetMapping("/main/layout2")
    public String layout2() {
        return "main/layout2";
    }

    @GetMapping("/todo/mylist")
    public String myllist() {
        return "todo/mylist";
    }

    @GetMapping("/todo/view")
    public String view() {
        return "todo/view";
    }

    @GetMapping("/todo/regist")
    public String regist() {
        return "todo/regist";
    }
}
