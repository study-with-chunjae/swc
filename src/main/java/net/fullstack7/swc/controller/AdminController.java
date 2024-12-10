package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.AdminServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.QnaServiceIf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
                return "redirect:/admin/memberList";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/login";
        }
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
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

    @PostMapping("/{memberId}/status")
    public ResponseEntity<String> updateMemberStatus(@PathVariable("memberId") String memberId,
                                                     @RequestParam String status) {
        int updatedCnt = memberService.updateStatusByMemberId(status, memberId);
        if (updatedCnt > 0) {
            return ResponseEntity.ok(memberId + "회원 상태 변경완료");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 회원을 찾을 수 없습니다.");
        }
    }

    @GetMapping("/qnaList")
    public String qnaList(Model model) {
        model.addAttribute("qnaList", qnaService.listQna());
        return "admin/qnaList";
    }

    @GetMapping("/qnaView/{qnaId}")
    public String qnaView(@PathVariable Integer qnaId,Model model) {
        QnaDTO qnaDTO = qnaService.adminViewQna(qnaId);
        model.addAttribute("qna", qnaDTO);
        return "admin/qnaView";
    }

    // 답변 등록 페이지 이동
    @GetMapping("/qnaAnswer/{qnaId}")
    public String answerQnaPage(@PathVariable Integer qnaId, Model model) {
        model.addAttribute("qnaId", qnaId);
        model.addAttribute("qnaDTO", new QnaDTO());
        return "admin/qnaAnswer";
    }

    // **답변 등록 처리 메서드 추가**
    @PostMapping("/{qnaId}/regist")
    public String registAnswer(@PathVariable Integer qnaId,
                               @ModelAttribute QnaDTO qnaDTO,
                               Model model) {
        qnaDTO.setParentId(qnaId);
        qnaService.addReply(qnaDTO, true);
        return "redirect:/admin/qnaList";
    }
}