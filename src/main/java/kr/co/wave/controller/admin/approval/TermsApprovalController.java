package kr.co.wave.controller.admin.approval;

import kr.co.wave.dto.approval.TermsApprovalDTO;
import kr.co.wave.service.approval.TermsApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TermsApprovalController {

    private final TermsApprovalService termsApprovalService;

    @GetMapping("/admin/approval/terms/list")
    public String index(@RequestParam(required = false) String searchType,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        Model model) {

        Page<TermsApprovalDTO> termsApprovalList = termsApprovalService.getTermsApprovalAllBySearch(searchType, keyword, page, 10);

        model.addAttribute("termsApprovalList", termsApprovalList);

        return "admin/approval/terms/list :: approvalTermsFragment";
    }

    @GetMapping("/admin/approval/terms/approval")
    public String approval(@RequestParam int termsId){

        termsApprovalService.approval(termsId);

        return "redirect:/admin/approval/terms/list";
    }

    @GetMapping("/admin/approval/terms/rejection")
    public String rejection(@RequestParam int termsId){

        termsApprovalService.rejection(termsId);

        return "redirect:/admin/approval/terms/list";
    }
}
