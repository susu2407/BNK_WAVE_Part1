package kr.co.wave.controller.admin.card;

import kr.co.wave.dto.card.CardWithInfoDTO;
import kr.co.wave.dto.card.CardRequestDTO;
import kr.co.wave.entity.config.Category;
import kr.co.wave.service.card.CardService;
import kr.co.wave.service.config.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.web.webauthn.api.PublicKeyCose;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService cardService;
    private final CategoryService categoryService;

    // 카드 목록
    @GetMapping("/admin/card/list")
    public String cardList(@RequestParam(required = false) String searchType,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String sortBy,
                           @RequestParam(defaultValue = "desc") String direction,
                           Model model) {

        Page<CardWithInfoDTO> cardList = cardService.getCardWithInfoAllBySearch(searchType, keyword, page, 10);

        model.addAttribute("cardList", cardList);
        return "admin/card/list";
    }

    // 카드 자세히 보기
    @GetMapping("/admin/card/view")
    public String cardView(@RequestParam String cardId, Model model) {

        CardWithInfoDTO card = cardService.getCardWithInfoById(Integer.parseInt(cardId));
        List<Category> categoryList = categoryService.getCategoryAll();

        model.addAttribute("cardItem", card);
        model.addAttribute("categoryList", categoryList);

        return "admin/card/view";
    }

    // 카드 수정 화면으로 이동
    @GetMapping("/admin/card/update")
    public String cardUpdate(@RequestParam String cardId, Model model) {
        CardWithInfoDTO card = cardService.getCardWithInfoById(Integer.parseInt(cardId));
        List<Category> categoryList = categoryService.getCategoryAll();

        model.addAttribute("cardItem", card);
        model.addAttribute("categoryList", categoryList);

        return "admin/card/update";
    }

    // 카드 수정
    @PostMapping("/admin/card/update")
    public String cardUpdate(int cardId, CardRequestDTO cardRequestDTO) {
        cardService.updateCard(cardId, cardRequestDTO);
        return "redirect:/admin/card/list";
    }

    // 카드 등록 화면으로 이동
    @GetMapping("/admin/card/register")
    public String cardRegister(Model model) {
        List<Category> categoryList = categoryService.getCategoryAll();

        model.addAttribute("categoryList", categoryList);

        return "admin/card/register";
    }

    // 카드 등록
    @PostMapping("/admin/card/register")
    public void cardRegister(CardRequestDTO cardRequestDTO) {

        cardService.registerCard(cardRequestDTO);
    }

    // 활성화
    @GetMapping("/admin/card/activate")
    public String cardActivate(@RequestParam String cardId) {

        cardService.activateCard(Integer.parseInt(cardId));

        return "redirect:/admin/card/list";
    }

    // 비활성화
    @PostMapping("/admin/card/inactivate")
    public String cardInactivate(@RequestParam String cardId, @RequestParam String reason) {

        cardService.inactivateCard(Integer.parseInt(cardId), reason);

        return "redirect:/admin/card/list";
    }

    // 상품관리 > 상품별 가입 현황 - 화면 이동
    @GetMapping("/admin/card/status")
    public String cardStatus() {
        return "admin/card/status";
    }
}
