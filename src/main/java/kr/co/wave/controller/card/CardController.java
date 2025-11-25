package kr.co.wave.controller.card;

import jakarta.servlet.http.HttpSession;
import kr.co.wave.dto.card.CardApplyDTO;
import kr.co.wave.dto.card.CardApplyRequestDTO;
import kr.co.wave.dto.card.CardWithInfoDTO;
import kr.co.wave.service.card.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;

    @GetMapping("/card/list")
    public String cardList(@RequestParam(defaultValue = "") String searchType,
                           @RequestParam(defaultValue = "") String keyword,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {

        // 서비스에서 카드 전체 , 혜택, 연회비 포함된 DTO가져오기 + card status = '활성' 만 가져오기
        Page<CardWithInfoDTO> cardPage = cardService.getCardWithInfoAllBySearch2(searchType, keyword, page, 12);

        model.addAttribute("cardPage", cardPage);
        model.addAttribute("cards", cardPage.getContent()); // 리스트만 가져오기


        return "card/list";
    }


    @GetMapping("/card/view")
    public String cardView(int cardId, Model model) {
        CardWithInfoDTO card = cardService.getCardWithInfoById(cardId);
        model.addAttribute("fff", card);

        return "card/view";
    }

    @GetMapping("/card/view2")
    public String viewCard(int cardId, Model model) {

        // 상세보기 페이지 메인쪽을 담당 cardId를 통해 "1개의 카드에 대한 정보 가져옴"
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        // 상세보기 페이지 메인 아래의 카드 비교함 - 비교카드 가져오기를 위해 전체 카드(status="활성")만 가져오기
        model.addAttribute("compareCards", cardService.getCardWithInfoAllBySearch2("", "", 0, 100).getContent());

        return "card/view2";
    }

    @GetMapping("/card/register1")
    public String register1(int cardId, Model model) {

        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register1";
    }

    @GetMapping("/card/register2")
    public String register2(int cardId, Model model) {

        log.info("=== register2 진입 ===");
        log.info("cardId: {}", cardId);

        // 카드 상품 조회
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        // 빈 폼 객체 (사용자 정보 입력 용)
        model.addAttribute("applyForm", new CardApplyDTO());


        log.info("카드 정보 조회 완료: {}", cardInfo);


        return "card/register2";


    }

    @PostMapping("/card/apply/step2") // 추후 변경예정 1121박효빈
    public String applystep2(CardApplyRequestDTO request, HttpSession session) {


        log.info("=== applyStep2 진입 ===");

        // 입력 받은 사용자 정보 세션에 임시 저장(why? 로그인 상태로도 이 페이지에서는 입력해야하니깐 일단 구현
        session.setAttribute("applyInfo", request);
        log.info("세션에 정보 저장 완료");
        log.info("받은 데이터 전체: {}", session.getAttribute("applyInfo"));


        return "redirect:/card/register3?cardId=" + request.getCardId();
    }


    @GetMapping("/card/register3")
    public String register3(@RequestParam int cardId, Model model) {
        log.info("=== register3 진입 ===");
        log.info("cardId: {}", cardId);

        // 카드 상품 조회 (뷰에서 cardItem을 사용하기 위해)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register3";
    }

//    @GetMapping("/card/register4")
//    public String register4() {
//
//        return "card/register4";
//    }

    @GetMapping("/card/register5")
    public String register4(@RequestParam int cardId, HttpSession session, Model model) {

        log.info("=== register5 진입 ===");
        log.info("cardId: {}", cardId);

        // 1. 세션에서 임시 저장된 신청 정보(applyInfo)를 불러옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청 단계 2로 돌려보내 재입력을 유도합니다.
            // 이 때, cardId는 유지하여 상품 정보는 다시 볼 수 있게 합니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }

        log.info("세션에서 불러온 CardApplyRequestDTO 정보: {}", applyInfo);

        // 2. 카드 상품 정보 조회 (화면 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        // 3. 다음 뷰에서 세션 데이터를 활용할 필요가 있다면 model에 추가합니다.
        // model.addAttribute("applyInfo", applyInfo);

        return "card/register5";
    }

    @PostMapping("/card/apply/step5")
    public String applystep5(@RequestParam int cardId,
                             CardApplyRequestDTO newInfo,
                             HttpSession session) {

        log.info("=== applyStep5 진입: 추가 정보 병합 ===");

        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션 정보 누락. Step 2로 리다이렉트합니다.");
            return "redirect:/card/register2?cardId=" + cardId;
        }

        // 새로운 정보를 기존 DTO에 setter로 병합
        applyInfo.setJob(newInfo.getJob());
        applyInfo.setRiskJob(newInfo.getRiskJob());
        applyInfo.setFundSource(newInfo.getFundSource());
        applyInfo.setPurpose(newInfo.getPurpose());

        session.setAttribute("applyInfo", applyInfo);
        log.info("세션 DTO 업데이트 완료. 최신 정보: {}", applyInfo);

        // 다음 단계(Step 6)로 이동합니다.
        return "redirect:/card/register6?cardId=" + cardId;
    }

    @GetMapping("/card/register6")
    public String register6(@RequestParam int cardId, HttpSession session, Model model) {
        log.info("=== register6 진입: 최종 DTO 유효성 검사 ===");
        log.info("cardId: {}", cardId);

        // 1. 세션에서 현재까지 누적된 신청 정보(applyInfo)를 가져옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.info("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청의 시작점인 Step 2로 돌려보냅니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }


        // 2. 카드 상품 정보 조회 (뷰 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        // 3. 뷰에서 사용하기 위해 최종 DTO를 모델에 담아 전달합니다. (선택적)
        // 예를 들어, 사용자 이름 등을 표시할 수 있습니다.
        model.addAttribute("applyInfo", applyInfo);
        log.info("세션에서 불러온 최종 신청 정보 확인: {}", applyInfo);

        return "card/register6";
    }

    @PostMapping("/card/apply/step6")
    public String applystep6(@RequestParam int cardId, CardApplyRequestDTO step6Info, HttpSession session) {

        log.info("=== applyStep6 진입 : 최종 정보 병합 ===");
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션 정보 누락. Step 2로 리다이렉트합니다.");
            return "redirect:/card/register2?cardId=" + cardId;
        }


        // step6에서 추가되는 사용자 입력 데이터를 setter 병합
        // 영문명 *수정 시 최종반영

        applyInfo.setLastNameEn(step6Info.getLastNameEn());
        applyInfo.setFirstNameEn(step6Info.getFirstNameEn());

        // 후볼교통 기능 신청 여부
        applyInfo.setPostpaidTransit(step6Info.getPostpaidTransit());

        // 해외 결제 여부 (dual/domestic)
        applyInfo.setOverseasUse(step6Info.getOverseasUse());

        // 추가 필드 입력된 DTO 다시 세션 저장
        session.setAttribute("applyInfo", applyInfo);

        log.info("세션 DTO 업데이트 완료. 최신정보 (POST STEP6:{}", applyInfo);

        //
        return "redirect:/card/register7?cardId=" + cardId;
    }

    @GetMapping("/card/register7")
    public String register7(@RequestParam int cardId, HttpSession session, Model model) {
        log.info("=== register7 진입: 최종 DTO 유효성 검사 ===");

        // 1. 세션에서 현재까지 누적된 신청 정보(applyInfo)를 가져옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.info("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청의 시작점인 Step 2로 돌려보냅니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }

        // 2. 카드 상품 정보 조회 (뷰 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register7";
    }

    @PostMapping("/card/apply/step7")
    public String applystep7(@RequestParam int cardId, CardApplyRequestDTO step7Info, HttpSession session) {

        log.info("=== applyStep6 진입 : 최종 정보 병합 ===");
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션 정보 누락. Step 2로 리다이렉트합니다.");
            return "redirect:/card/register2?cardId=" + cardId;
        }



        applyInfo.setAccountBank(step7Info.getAccountBank());
        applyInfo.setAccountNumber(step7Info.getAccountNumber());

        if (step7Info.getAccountVerified() == null) {
            applyInfo.setAccountVerified(0);

        }

        // 추가 필드 입력된 DTO 다시 세션 저장
        session.setAttribute("applyInfo", applyInfo);

        log.info("세션 DTO 업데이트 완료. 최신정보 (POST STEP7:{}", applyInfo);


        return "redirect:/card/register8?cardId=" + cardId;

    }

    @GetMapping("/card/register8")
    public String register8(@RequestParam int cardId, HttpSession session, Model model) {
        log.info("=== register8 진입: 최종 DTO 유효성 검사 ===");

        // 1. 세션에서 현재까지 누적된 신청 정보(applyInfo)를 가져옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.info("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청의 시작점인 Step 2로 돌려보냅니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }

        // 2. 카드 상품 정보 조회 (뷰 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register8";
    }

    @PostMapping("/card/apply/step8")
    public String applystep8(@RequestParam int cardId, CardApplyRequestDTO step8Info, HttpSession session) {

        log.info("=== applyStep8 진입 : 최종 정보 병합 ===");
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션 정보 누락. Step 2로 리다이렉트합니다.");
            return "redirect:/card/register2?cardId=" + cardId;
        }



        applyInfo.setPin(step8Info.getPin()); // 카드 비밀번호 저장
      
        

        // 추가 필드 입력된 DTO 다시 세션 저장
        session.setAttribute("applyInfo", applyInfo);

        log.info("세션 DTO 업데이트 완료. 최신정보 (POST STEP8 PIN 번호?:{}", applyInfo);


        return "redirect:/card/register9?cardId=" + cardId;

    }


    @GetMapping("/card/register9")
    public String register9(@RequestParam int cardId, HttpSession session, Model model) {
        log.info("=== register9 진입: 최종 DTO 유효성 검사 === 카드 주소지 입력창");

        // 1. 세션에서 현재까지 누적된 신청 정보(applyInfo)를 가져옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.info("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청의 시작점인 Step 2로 돌려보냅니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }

        // 2. 카드 상품 정보 조회 (뷰 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register9";
    }

    @PostMapping("/card/apply/step9")
    public String applystep9(@RequestParam int cardId, CardApplyRequestDTO step9Info, HttpSession session) {

        log.info("=== applyStep9 진입 : 최종 정보 병합 ===");
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.warn("세션 정보 누락. Step 2로 리다이렉트합니다.");
            return "redirect:/card/register2?cardId=" + cardId;
        }



        applyInfo.setZip(step9Info.getZip()); // 우편번호 저장
        applyInfo.setAddr1(step9Info.getAddr1());
        applyInfo.setAddr2(step9Info.getAddr2());


        // 추가 필드 입력된 DTO 다시 세션 저장
        session.setAttribute("applyInfo", applyInfo);

        log.info("세션 DTO 업데이트 완료. 최신정보 (POST STEP9 집주소 뜨냐? :{}", applyInfo);


        return "redirect:/card/register10?cardId=" + cardId;

    }


    @GetMapping("/card/register10")
    public String register10(@RequestParam int cardId, HttpSession session, Model model) {
        log.info("=== register10 진입: 최종 DTO 유효성 검사 === 카드 주소지 완료 !! ");

        // 1. 세션에서 현재까지 누적된 신청 정보(applyInfo)를 가져옵니다.
        CardApplyRequestDTO applyInfo = (CardApplyRequestDTO) session.getAttribute("applyInfo");

        if (applyInfo == null) {
            log.info("세션에 'applyInfo'가 없습니다. 비정상적인 접근이거나 세션이 만료되었습니다.");
            // 세션 정보가 없으면, 신청의 시작점인 Step 2로 돌려보냅니다.
            return "redirect:/card/register2?cardId=" + cardId;
        }

        // 2. 카드 상품 정보 조회 (뷰 표시용)
        CardWithInfoDTO cardInfo = cardService.getCardWithInfoById(cardId);
        model.addAttribute("cardItem", cardInfo);

        return "card/register10";
    }


}
