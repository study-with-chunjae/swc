package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.service.AdminServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceIf adminService;
    private final QnaServiceIf qnaService;
    private final MemberServiceIf memberService;

    @GetMapping("/main")
    public String main() {
        return "admin/main";
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(AdminDTO adminDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            if (adminService.login(adminDTO.getAdminId(), adminDTO.getPassword())) {
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

    @GetMapping("/bbsList")
    public String bbsList() {
        return "admin/bbsList";
    }

    @GetMapping("/memberList")
    public String memberList(@RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
                          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                          @PageableDefault(size = 10) Pageable pageable,
                          Model model) {
        Page<AdminMemberDTO> memberPage = memberService.getAllMembers(searchType, keyword, pageable);
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/memberList";
    }

    @GetMapping("/qnaList")
    public String qnaList(Model model) {
        model.addAttribute("qnaList", qnaService.listQna());
        return "admin/qnaList";
    }

}