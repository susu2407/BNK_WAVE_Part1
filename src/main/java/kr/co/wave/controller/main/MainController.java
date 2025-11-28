package kr.co.wave.controller.main;


import kr.co.wave.dto.card.CardWithInfoDTO;
import kr.co.wave.dto.card.PopularCardDTO;
import kr.co.wave.service.card.CardService;
import kr.co.wave.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CardService cardService;
    private final MemberService memberService;

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "") String searchType,
                        @RequestParam(defaultValue = "") String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        // 1. 서비스에서 카드 전체 , 혜택, 연회비 포함된 DTO가져오기 + card status = '활성' 만 가져오기
        Page<CardWithInfoDTO> cardPage = cardService.getCardWithInfoAllBySearch2(searchType, keyword,page,12);


        // 서비스에서 인기카드 항목 가져오기
        List<PopularCardDTO> popularCards = cardService.getPopularCards();
        model.addAttribute("popularCards", popularCards);

        // 2. ✨ 메인 페이지 추천/슬라이더용 랜덤 카드 4개 가져오기
        List<CardWithInfoDTO> randomCards = cardService.getRandom4ActiveCards();
        model.addAttribute("randomCards", randomCards); //

        model.addAttribute("cardPage", cardPage);
        model.addAttribute("cards",cardPage.getContent()); // 리스트만 가져오기

        return "index";
    }

}
