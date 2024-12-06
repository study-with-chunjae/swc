package net.fullstack7.swc.controller;

import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/post")
public class PostController {
    private static final String CONTROLLER_NAME = "PostController";
    private static final String DEFAULT_REDIRECT = "redirect:/post/list";
    @GetMapping("/main")
    public String postMain(){
        LogUtil.logLine(CONTROLLER_NAME + "main");
        return "post/main";
    }
    @GetMapping("/list")
    public String list() {
        LogUtil.logLine(CONTROLLER_NAME + "list");
        return "post/list";
    }
    @GetMapping("/view")
    public String view() {
        LogUtil.logLine(CONTROLLER_NAME + "view");
        return "post/view";
    }

    @GetMapping("/register")
    public String registerGet() {
        LogUtil.logLine(CONTROLLER_NAME + "register get");
        return "post/register";
    }
    @PostMapping("/register")
    public String registerPost(){
        LogUtil.logLine(CONTROLLER_NAME + "register post");
        return "post/view";
    }

    @GetMapping("/modify")
    public String modifyGet() {
        LogUtil.logLine(CONTROLLER_NAME + "modify");
        return "post/modify";
    }
    @PostMapping("/modify")
    public String modifyPost(){
        LogUtil.logLine(CONTROLLER_NAME + "modify post");
        return "post/view";
    }


}
