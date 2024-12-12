package net.fullstack7.swc.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Qna;
import net.fullstack7.swc.dto.AdminDTO;
import net.fullstack7.swc.dto.AdminMemberDTO;
import net.fullstack7.swc.dto.QnaDTO;
import net.fullstack7.swc.service.AdminServiceIf;
import net.fullstack7.swc.service.MemberServiceIf;
import net.fullstack7.swc.service.QnaServiceIf;
import net.fullstack7.swc.util.ErrorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final AdminServiceIf adminService;
    private final QnaServiceIf qnaService;
    private final MemberServiceIf memberService;
    private final ErrorUtil errorUtil;

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
    public String memberList(
            @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size, // 페이지 당 항목 수
            Model model
    ) {

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
        @RequestParam String status) {
        int updatedCnt = memberService.updateStatusByMemberId(status, memberId);
        if (updatedCnt > 0) {
            return ResponseEntity.ok(memberId + "회원 상태 변경완료");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 회원을 찾을 수 없습니다.");
        }
    }

    @GetMapping("/qnaList")
    public String qnaList(
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size, // 페이지 당 항목 수
            @RequestParam(value = "answered", required = false) Boolean answered, // 필터링 옵션
            Model model
    ) {
        // Pageable 객체 생성 (페이지 번호, 크기, 정렬 기준)
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());

        Page<QnaDTO> qnaPage;

        // answered 필터링이 있는 경우와 없는 경우 분기
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
                               @ModelAttribute @Valid QnaDTO qnaDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("qnaDTO", qnaDTO);
            return errorUtil.redirectWithError("/qna/regist", redirectAttributes, bindingResult);
        }
        qnaDTO.setParentId(qnaId);
        qnaService.addReply(qnaDTO, true);
        return "redirect:/admin/qnaList";
    }
}