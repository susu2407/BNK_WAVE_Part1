package kr.co.wave.controller.admin.member;

import kr.co.wave.entity.member.Member;
import kr.co.wave.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberManagerController {

    private final MemberService memberService;

    // 관리자 로그인 페이지로 이동
    @GetMapping("/admin/member/list")
    public String login(@RequestParam(required = false) String searchType,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        Page<Member> memberList = memberService.getMemberAll(searchType, keyword, page, 10);

        model.addAttribute("memberList", memberList);

        return "admin/member/list";
    }

}
