package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.service.AdminServiceIf;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceIf adminService;
    private final QnaServiceIf qnaService;

    @GetMapping("/main")
    public String main() {
        return "admin/main";
    }
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * 로그인 페이지를 만든다.
     * 아이디 파라미터는 AdminDTO 와 맞게 만든다.
     * ex) id input 의 name 속성은 adminId,
     * 비번 input 의 name 속성은 password 로 만들면 알아서 바인딩된다 ㅅㄱ
     *
     */
    @PostMapping("/login")
    public String login(AdminDTO adminDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            if(adminService.login(adminDTO.getAdminId(),adminDTO.getPassword())) {
                session.setAttribute("admin", adminDTO.getAdminId());
                return "redirect:/admin/dashboard";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/login";
        }
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String login(HttpSession session) {
        session.invalidate();
        return "admin/login";
    }

    @GetMapping("/memberList")
    public String memberList() {
        return "admin/memberList";
    }
    @GetMapping("/bbsList")
    public String bbsList() {
        return "admin/bbsList";
    }

    @GetMapping("/qnaList")
    public String qnaList(Model model) {
        model.addAttribute("qnaList", qnaService.listQna());
        return "admin/qnaList";
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
