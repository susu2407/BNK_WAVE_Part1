package kr.co.wave.controller.admin.approval;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ApprovalController {

    // 관리자 승인 페이지로 이동
    @GetMapping("/admin/approval/approval")
    public String approvalPage () {
        return "admin/approval/approval";
    }

    // 관리자 승인-카드 가입 승인 페이지로 이동
    @GetMapping("/admin/approval/member/cardList")
    public String approvalMemberCard () {
        return "admin/approval/member/cardList";
    }



}
