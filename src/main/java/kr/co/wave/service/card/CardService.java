package kr.co.wave.service.card;

import jakarta.transaction.Transactional;
import kr.co.wave.dto.approval.CardApprovalDTO;
import kr.co.wave.dto.card.*;
import kr.co.wave.entity.approval.CardApproval;
import kr.co.wave.entity.card.*;
import kr.co.wave.entity.member.Address;
import kr.co.wave.entity.member.Member;
import kr.co.wave.repository.approval.CardApprovalRepository;
import kr.co.wave.repository.card.*;
import kr.co.wave.repository.member.AddressRepository;
import kr.co.wave.repository.member.MemberRepository;
import kr.co.wave.service.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AnnualFeeRepository annualFeeRepository;
    private final BenefitRepository benefitRepository;
    private final CardApprovalRepository cardApprovalRepository;
    private final ModelMapper modelMapper; // Entity와 DTO를 변환해주는 객체
    private final FileUploadUtil fileUploadUtil; // 저기 util 불러오는 객체
    private final MemberRepository memberRepository;
    private final MemberCardRepository memberCardRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    // 필요없는데 혹시나 남겨둠
    public Page<CardDTO> getCardAllBySearch(String searchType, String keyword, int page, int size){
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findCardAllBySearch(st, kw, pageable);
    }
    
    // 필요 없어질 예정
    public CardDTO getCardById(int cardId){
        Optional<Card> card = cardRepository.findById(cardId);
        if(card.isPresent()){
            return modelMapper.map(card.get(), CardDTO.class);
        }
        return null;
    }

    // 카드 하나만 가져오기 (혜택, 연회비 포함)
    @Transactional
    public CardWithInfoDTO getCardWithInfoById(int cardId) {
        Card card = cardRepository.findById(cardId).get();

        CardWithInfoDTO cardWithInfoDTO = new CardWithInfoDTO();
        cardWithInfoDTO.setCard(modelMapper.map(card, CardDTO.class));

        List<Benefit> benefits = benefitRepository.findByCard_CardId(card.getCardId());
        cardWithInfoDTO.setBenefitList(benefits);

        List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(card.getCardId());
        cardWithInfoDTO.setAnnualFeeList(annualFees);

        return cardWithInfoDTO;
    }


    // 카드 전체 페이지 타입으로 가져오기 (혜택, 연회비 포함) // admin 용
    @Transactional
    public Page<CardWithInfoDTO> getCardWithInfoAllBySearch(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        // DB에서 검색 + 페이징된 카드 목록 가져오기
        Page<CardDTO> cardPage = cardRepository.findCardAllBySearch(st, kw, pageable);

        // 각 카드에 혜택 / 연회비 정보 붙이기
        List<CardWithInfoDTO> adminCardList = new ArrayList<>();

        for (CardDTO cardDTO : cardPage.getContent()) {
            CardWithInfoDTO dto = new CardWithInfoDTO();

            // 카드 기본정보
            dto.setCard(cardDTO);

            // 혜택 목록
            List<Benefit> benefits = benefitRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setBenefitList(benefits);

            // 추가 11.17 박효빈 혜택 카테고리 - Thymeleaf에서 처리 못하는 부분 service에서 해결
            List<String> categoryList = benefits.stream()
                    .map(Benefit::getBenefitCategory)
                    .toList();
            dto.setCategoryString(String.join(",", categoryList));


            // 연회비 목록
            List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setAnnualFeeList(annualFees);

            CardApprovalDTO cardApprovalDTO = cardApprovalRepository.findCardApprovalPendingByCardId(cardDTO.getCardId());
            if(cardApprovalDTO != null){
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminCardList.add(dto);
        }

        // Page 객체로 다시 감싸서 반환 (검색결과, 페이지 수 그대로 유지)
        return new PageImpl<>(adminCardList, pageable, cardPage.getTotalElements());
    }

    @Transactional // 사용자 페이지용 status='활성'만가져오기
    public Page<CardWithInfoDTO> getCardWithInfoAllBySearch2(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        // DB에서 검색 + 페이징된 카드 목록 가져오기
        Page<CardDTO> cardPage = cardRepository.findCardAllBySearch2(st, kw, pageable);

        // 각 카드에 혜택 / 연회비 정보 붙이기
        List<CardWithInfoDTO> adminCardList = new ArrayList<>();

        for (CardDTO cardDTO : cardPage.getContent()) {
            CardWithInfoDTO dto = new CardWithInfoDTO();

            // 카드 기본정보
            dto.setCard(cardDTO);

            // 혜택 목록
            List<Benefit> benefits = benefitRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setBenefitList(benefits);

            // 추가 11.17 박효빈 혜택 카테고리 - Thymeleaf에서 처리 못하는 부분 service에서 해결
            List<String> categoryList = benefits.stream()
                    .map(Benefit::getBenefitCategory)
                    .toList();
            dto.setCategoryString(String.join(",", categoryList));


            // 연회비 목록
            List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setAnnualFeeList(annualFees);

            CardApprovalDTO cardApprovalDTO = cardApprovalRepository.findCardApprovalPendingByCardId(cardDTO.getCardId());
            if(cardApprovalDTO != null){
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminCardList.add(dto);
        }

        // Page 객체로 다시 감싸서 반환 (검색결과, 페이지 수 그대로 유지)
        return new PageImpl<>(adminCardList, pageable, cardPage.getTotalElements());
    }

    // 카드 등록
    // 카드 이미지 등록 추가 ( 11.17 박효빈)
    @Transactional
    public void registerCard(CardRequestDTO cardRequestDTO){

        // 추가 코드 ( TMI:이미지 저장하는 코드 )
        String thumbnailPath = fileUploadUtil.saveFile(cardRequestDTO.getThumbnail(),"card");
        String backgroundPath = fileUploadUtil.saveFile(cardRequestDTO.getBackground(),"card");

        // 카드
        Card card = Card.builder()
                .name(cardRequestDTO.getName())
                .engName(cardRequestDTO.getEngName())
                .type(cardRequestDTO.getType())
                .isCompany(cardRequestDTO.isCompany())
                .description(cardRequestDTO.getDescription())
                // 카드 이미지 업로드 추가 (11.17)
                .thumbnail(thumbnailPath)
                .background(backgroundPath)
                .status("비활성")
                .build();

        System.out.println(card.toString());
        Card savedCard = cardRepository.save(card);

        // 연회비
        for(int i = 0; i<cardRequestDTO.getAnnualFeeName().size(); i++) {
            AnnualFee annualFee = AnnualFee.builder()
                                                    .annualName(cardRequestDTO.getAnnualFeeName().get(i))
                                                    .card(savedCard)
                                                    .build();

            System.out.println(annualFee.toString());

            annualFeeRepository.save(modelMapper.map(annualFee, AnnualFee.class));
        }

        // 혜택
        for(int i = 0; i<cardRequestDTO.getCategory().size(); i++){
            Benefit benefit = Benefit.builder()
                    .benefitType(cardRequestDTO.getBenefitType().get(i))
                    .benefitCategory(String.valueOf(cardRequestDTO.getCategory().get(i)))
                    .unit(cardRequestDTO.getUnit().get(i))
                    .value(cardRequestDTO.getValue().get(i))
                    .limit(cardRequestDTO.getLimit().get(i))
                    .benefitDescription(cardRequestDTO.getBenefitDescription().get(i))
                    .card(savedCard)
                    .build();

            System.out.println(i + "번째" + benefit.toString());

            benefitRepository.save(benefit);
        }
    }

    @Transactional
    public void updateCard(int cardId, CardRequestDTO cardRequestDTO) {

        // 1. 기존 카드 조회
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));

        // 2. 이미지 업데이트
        String thumbnailPath = card.getThumbnail();
        String backgroundPath = card.getBackground();

        // 새로운 파일이 들어온 경우만 교체
        if (cardRequestDTO.getThumbnail() != null && !cardRequestDTO.getThumbnail().isEmpty()) {
            thumbnailPath = fileUploadUtil.saveFile(cardRequestDTO.getThumbnail(), "card");
        }

        if (cardRequestDTO.getBackground() != null && !cardRequestDTO.getBackground().isEmpty()) {
            backgroundPath = fileUploadUtil.saveFile(cardRequestDTO.getBackground(), "card");
        }

        // 3. 카드 정보 업데이트
        Card updatedCard = Card.builder()
                .cardId(card.getCardId())   // 기존 식별자 유지 → UPDATE
                .name(cardRequestDTO.getName())
                .engName(cardRequestDTO.getEngName())
                .type(cardRequestDTO.getType())
                .isCompany(cardRequestDTO.isCompany())
                .description(cardRequestDTO.getDescription())
                .thumbnail(thumbnailPath)
                .background(backgroundPath)
                .status(card.getStatus())  // 기존 상태 유지
                .updatedAt(LocalDate.now())
                .build();

        cardRepository.save(updatedCard);

        // 4. 연회비 삭제 후 새로 저장
        annualFeeRepository.deleteByCard(card);

        for (int i = 0; i < cardRequestDTO.getAnnualFeeName().size(); i++) {
            AnnualFee annualFee = AnnualFee.builder()
                    .annualName(cardRequestDTO.getAnnualFeeName().get(i))
                    .card(card)
                    .build();

            annualFeeRepository.save(annualFee);
        }

        // 5. 혜택 삭제 후 새로 저장
        benefitRepository.deleteByCard(card);

        for (int i = 0; i < cardRequestDTO.getCategory().size(); i++) {
            Benefit benefit = Benefit.builder()
                    .benefitType(cardRequestDTO.getBenefitType().get(i))
                    .benefitCategory(String.valueOf(cardRequestDTO.getCategory().get(i)))
                    .unit(cardRequestDTO.getUnit().get(i))
                    .value(cardRequestDTO.getValue().get(i))
                    .limit(cardRequestDTO.getLimit().get(i))
                    .benefitDescription(cardRequestDTO.getBenefitDescription().get(i))
                    .card(card)
                    .build();

            benefitRepository.save(benefit);
        }
    }


    // 카드 활성화
    @Transactional
    public void activateCard(int cardId){
        Optional<Card> OptionalCard = cardRepository.findById(cardId);

        if(OptionalCard.isPresent()){
            Card card = OptionalCard.get();
            card.toggleStatus("활성");
        }
    }

    // 카드 비활성화 요청
    @Transactional
    public void inactivateCard(int cardId, String reason){

        CardApproval cardApproval = CardApproval.builder().
                cardId(cardId).
                reason(reason).
                status("대기").
                build();

        cardApprovalRepository.save(cardApproval);
    }

    // 11.27 추가된 메서드: 로그인 사용자의 카드 신청 정보 초기화 (Step 5 시작용)
    public CardApplyRequestDTO initializeApplyInfoForLoggedInUser(String memId, int cardId) {
        // 1. 회원 ID(memId)를 사용하여 DB에서 Member 엔티티를 조회합니다.
        Optional<Member> optionalMember = memberRepository.findById(memId);

        // 2. 조회 결과가 없을 경우 처리
        if (optionalMember.isEmpty()) {
            log.warn("로그인된 사용자 ID ({})에 해당하는 Member 정보가 DB에 없습니다. 빈 DTO를 반환합니다.", memId);
            CardApplyRequestDTO emptyDto = new CardApplyRequestDTO();
            emptyDto.setCardId(cardId);
            return emptyDto;
        }

        // 3. Member 엔티티를 추출합니다.
        Member member = optionalMember.get();

        // 4. DB의 Member 정보를 CardApplyRequestDTO 필드에 매핑합니다.
        // Member 정보를 Step 2 필수 정보와 Step 6/9 정보를 포함하여 DTO에 매핑
        CardApplyRequestDTO dto = CardApplyRequestDTO.builder()
                .cardId(cardId)
                // Step 2 정보: 사용자 이름, 이메일, 주민등록번호
                .name(member.getName())
                .email(member.getEmail())
                .rrn(member.getRrn())
                // Step 6 정보: 영문 이름 (DB에 저장되어 있다면)
                .firstNameEn(member.getFirstNameEn())
                .lastNameEn(member.getLastNameEn())
                // Step 9 정보: 주소 (DB에 저장되어 있다면)
                .zip(member.getZip())
                .addr1(member.getAddress())
                .addr2(member.getDeaddress())
                // 이외의 필드는 Step 5 이후 단계에서 사용자 입력으로 채워질 예정
                .build();

        return dto;
    }

    // 카드 상품 가입 (사용자 정보 임시저장 CardApplyRequestDTO)
    @Transactional
    public void applyCard(CardApplyRequestDTO dto, String memId) {

        System.out.println("전달된 memId: " + memId);

        String guestRole = "GENERAL";
        boolean isNewMember = false;

        if (memId == null || memId.isEmpty()) {
            // 비로그인 사용자용 임시 ID 생성
            memId = "guest_" + UUID.randomUUID();
            isNewMember = true;
        }

        // 1️⃣ Member 확인 / 생성 또는 업데이트
        Optional<Member> optionalMember = memberRepository.findById(memId);
        Member member;

        if (optionalMember.isPresent()) {
            //기존 멤버: 엔티티의 전용 업데이트 메서드 호출
            member = optionalMember.get();
            member.updateApplicationInfo(
                    dto.getName(),
                    dto.getFirstNameEn(),
                    dto.getLastNameEn(),
                    dto.getEmail(),
                    dto.getRrn(),
                    dto.getAddr1(), // address
                    dto.getAddr2(), // deaddress
                    dto.getZip()    // zip
            );
            // @Transactional에 의해 메서드 종료 시 자동 반영됨 (Dirty Checking)

        } else {
            //  신규 멤버: Builder를 통해 객체 생성 🌟
            member = Member.builder()
                    .memId(memId)
                    .name(dto.getName())
                    .firstNameEn(dto.getFirstNameEn())
                    .lastNameEn(dto.getLastNameEn())
                    .email(dto.getEmail())
                    .rrn(dto.getRrn())
                    .zip(dto.getZip())
                    .address(dto.getAddr1())
                    .deaddress(dto.getAddr2())
                    .status("활성")
                    .role(guestRole) // 신규 멤버에게만 역할 할당
                    .build();

            memberRepository.save(member);
        }

        // Account 저장
        Account account = Account.builder()
                .memId(memId)
                .accountBank(dto.getAccountBank())
                .accountNumber(dto.getAccountNumber())
                .accountVerified(dto.getAccountVerified())
                .pin(dto.getPin())
                .build();
        Account savedAccount = accountRepository.save(account);

        // MemberCard 저장
        MemberCard memberCard = MemberCard.builder()
                .memId(memId)
                .cardId(dto.getCardId())
                .accountId(String.valueOf(savedAccount.getAccountId()))
                .expiredAt(LocalDate.now().plusYears(3))
                .status("비활성")
                .build();
        memberCardRepository.save(memberCard);

        // Address 저장 (새로운 배송지 또는 정보 저장)
        Address address = Address.builder()
                .memId(memId)
                .zip(dto.getZip())
                .addr1(dto.getAddr1())
                .addr2(dto.getAddr2())
                .build();
        addressRepository.save(address);
    }


    @Transactional
    public List<Card> getTypeCredit() {
        return cardRepository.findByTypeCredit();
    }

    @Transactional
    public List<Card> getTypeCheck() {
        return cardRepository.findByTypeCheck();
    }
    // 인기카드 (PopularCardDTO) - MemberCardRepositoryd와 연결
    public List<PopularCardDTO> getPopularCards() {
        return memberCardRepository.findPopularCards()
                .stream()
                .limit(4)
                .toList();
    }

    // 랜덤 카드 4개 뽑아오기 추가용
    @Transactional
    public List<CardWithInfoDTO> getRandom4ActiveCards() {
        // 1. Pageable 객체를 사용하여 상위 4개만 가져오도록 요청
        Pageable top4 = PageRequest.of(0, 4);

        // Repository에서 무작위 활성 카드 4개만 가져오기
        List<CardDTO> random4Cards = cardRepository.findRandomCardsWithCustomSort("활성", top4);

        // 2. 각 카드에 혜택 / 연회비 정보 붙이기
        List<CardWithInfoDTO> randomCardList = new ArrayList<>();

        for (CardDTO cardDTO : random4Cards) {
            CardWithInfoDTO dto = new CardWithInfoDTO();

            // 카드 기본정보
            dto.setCard(cardDTO);

            // 혜택 목록
            List<Benefit> benefits = benefitRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setBenefitList(benefits);

            // 혜택 카테고리
            List<String> categoryList = benefits.stream()
                    .map(Benefit::getBenefitCategory)
                    .toList();
            dto.setCategoryString(String.join(",", categoryList));

            // 연회비 목록
            List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setAnnualFeeList(annualFees);

            randomCardList.add(dto);
        }

        return randomCardList;
    }


    // 카드 전체 가져오기 (혜택, 연회비 포함) // admin 용
    @Transactional
    public List<CardWithInfoDTO> findCardAllWhereCredit() {

        // DB에서 검색 + 페이징된 카드 목록 가져오기
        List<CardDTO> cardList = cardRepository.findCardAllWhereCredit();

        // 각 카드에 혜택 / 연회비 정보 붙이기
        List<CardWithInfoDTO> adminCardList = new ArrayList<>();

        for (CardDTO cardDTO : cardList) {
            CardWithInfoDTO dto = new CardWithInfoDTO();

            // 카드 기본정보
            dto.setCard(cardDTO);

            // 혜택 목록
            List<Benefit> benefits = benefitRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setBenefitList(benefits);

            List<String> categoryList = benefits.stream()
                    .map(Benefit::getBenefitCategory)
                    .toList();
            dto.setCategoryString(String.join(",", categoryList));


            List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setAnnualFeeList(annualFees);

            CardApprovalDTO cardApprovalDTO = cardApprovalRepository.findCardApprovalPendingByCardId(cardDTO.getCardId());
            if (cardApprovalDTO != null) {
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminCardList.add(dto);
        }

        return adminCardList;
    }

    // 카드 전체 가져오기 (혜택, 연회비 포함) // admin 용
    @Transactional
    public List<CardWithInfoDTO> findCardAllWhereCheck() {

        // DB에서 검색 + 페이징된 카드 목록 가져오기
        List<CardDTO> cardList = cardRepository.findCardAllWhereCheck();

        // 각 카드에 혜택 / 연회비 정보 붙이기
        List<CardWithInfoDTO> adminCardList = new ArrayList<>();

        for (CardDTO cardDTO : cardList) {
            CardWithInfoDTO dto = new CardWithInfoDTO();

            // 카드 기본정보
            dto.setCard(cardDTO);

            // 혜택 목록
            List<Benefit> benefits = benefitRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setBenefitList(benefits);

            List<String> categoryList = benefits.stream()
                    .map(Benefit::getBenefitCategory)
                    .toList();
            dto.setCategoryString(String.join(",", categoryList));


            List<AnnualFee> annualFees = annualFeeRepository.findByCard_CardId(cardDTO.getCardId());
            dto.setAnnualFeeList(annualFees);

            CardApprovalDTO cardApprovalDTO = cardApprovalRepository.findCardApprovalPendingByCardId(cardDTO.getCardId());
            if (cardApprovalDTO != null) {
                dto.setApprovalStatus("결재진행중");
            } else dto.setApprovalStatus("");

            adminCardList.add(dto);
        }

        return adminCardList;
    }
}



