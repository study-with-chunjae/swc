package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.AdminServiceIf;
import net.fullstack7.swc.service.MemberServiceImpl;
import net.fullstack7.swc.service.QnaServiceIf;
import net.fullstack7.swc.util.ErrorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceIf adminService;
    private final QnaServiceIf qnaService;
    private final MemberServiceImpl memberService;
    private final ErrorUtil errorUtil;

    // 세션 체크 메서드
    private String checkLogin(HttpSession session, RedirectAttributes redirectAttributes) {
        String admin = (session != null) ? (String) session.getAttribute("admin") : null;
        if (admin == null) {
            redirectAttributes.addFlashAttribute("errors", "관리자 로그인이 필요합니다.");
            return "redirect:/admin/login";
        }
        return null;
    }

    @GetMapping("/main")
    public String main(HttpSession session, RedirectAttributes redirectAttributes) {
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }
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
            redirectAttributes.addFlashAttribute("errors", e.getMessage());
            return "redirect:/admin/login";
        }
        redirectAttributes.addFlashAttribute("errors", "잘못된 자격 증명입니다.");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/memberList")
    public String memberList(
            @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size, // 페이지 당 항목 수
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AdminMemberDTO> memberPage = memberService.getAllMembers(searchType, keyword, pageable);

        int totalPages = memberPage.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }

        if (page >= totalPages && totalPages > 0) {
            return "redirect:/admin/memberList?page=" + (totalPages - 1) +
                    "&size=" + size +
                    "&searchType=" + searchType +
                    "&keyword=" + keyword;
        }

        model.addAttribute("memberPage", memberPage);
        model.addAttribute("currentPage", memberPage.getNumber());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", totalPages); // 추가: 총 페이지 수

        return "admin/memberList";
    }

    @PostMapping("/{memberId}/status")
    public ResponseEntity<String> updateMemberStatus(@PathVariable("memberId") String memberId,
                                                     @RequestParam String status,
                                                     HttpSession session,
                                                     RedirectAttributes redirectAttributes) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 로그인이 필요합니다.");
        }

        try {
            if ("D".equalsIgnoreCase(status)) {
                memberService.deleteMember(memberId);
                return ResponseEntity.ok(memberId + " 회원이 성공적으로 탈퇴되었습니다.");
            } else {
                int updatedCnt = memberService.updateStatusByMemberId(status, memberId);
                if (updatedCnt > 0) {
                    return ResponseEntity.ok(memberId + " 회원 상태가 변경되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("해당 ID의 회원을 찾을 수 없습니다.");
                }
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/qnaList")
    public String qnaList(
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size, // 페이지 당 항목 수
            @RequestParam(value = "answered", required = false) Boolean answered, // 필터링 옵션
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());

        Page<QnaDTO> qnaPage;

        if (answered != null) {
            qnaPage = qnaService.listQnaByAnsweredPage(pageable, answered);
        } else {
            qnaPage = qnaService.listQnaPage(pageable);
        }

        int totalPages = qnaPage.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }

        if (page >= totalPages && totalPages > 0) {
            return "redirect:/admin/qnaList?page=" + (qnaPage.getTotalPages() - 1) + "&size=" + size + (answered != null ? "&answered=" + answered : "");
        }
        model.addAttribute("qnaPage", qnaPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("answered", answered);

        return "admin/qnaList";
    }

    @GetMapping("/qnaView/{qnaId}")
    public String qnaView(@PathVariable Integer qnaId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }

        QnaDTO qnaDTO = qnaService.adminViewQna(qnaId);
        model.addAttribute("qna", qnaDTO);
        return "admin/qnaView";
    }

    // 답변 등록 페이지 이동
    @GetMapping("/qnaAnswer/{qnaId}")
    public String answerQnaPage(@PathVariable Integer qnaId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("qnaId", qnaId);
        model.addAttribute("qnaDTO", new QnaDTO());
        return "admin/qnaAnswer";
    }

    // **답변 등록 처리 메서드 추가**
    @PostMapping("/{qnaId}/regist")
    public String registAnswer(@PathVariable Integer qnaId,
                               @ModelAttribute @Valid QnaDTO qnaDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpSession session,
                               Model model) {
        // 세션 체크
        String redirect = checkLogin(session, redirectAttributes);
        if (redirect != null) {
            return redirect;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("qnaDTO", qnaDTO);
            return errorUtil.redirectWithError("/qna/regist", redirectAttributes, bindingResult);
        }
        qnaDTO.setParentId(qnaId);
        qnaService.addReply(qnaDTO, true);
        return "redirect:/admin/qnaList";
    }
}
