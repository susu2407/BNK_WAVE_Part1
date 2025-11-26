package kr.co.wave.controller.admin.approval;

import kr.co.wave.dto.approval.CardApprovalDTO;
import kr.co.wave.service.approval.CardApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CardApprovalController {

    private final CardApprovalService cardApprovalService;

    @GetMapping("/admin/approval/card/list")
    public String index(@RequestParam(required = false) String searchType,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        Model model) {

        Page<CardApprovalDTO> cardApprovalList = cardApprovalService.getCardApprovalAllBySearch(searchType, keyword, page, 10);

        model.addAttribute("cardApprovalList", cardApprovalList);

        return "admin/approval/card/list";
    }

    @GetMapping("/admin/approval/card/approval")
    public String approval(@RequestParam int cardId){

        cardApprovalService.approval(cardId);

        return "redirect:/admin/approval/card/list";
    }

    @GetMapping("/admin/approval/card/rejection")
    public String rejection(@RequestParam int cardId){

        cardApprovalService.rejection(cardId);

        return "redirect:/admin/approval/card/list";
    }
}
