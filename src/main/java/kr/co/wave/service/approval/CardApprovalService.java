package kr.co.wave.service.approval;

import jakarta.transaction.Transactional;
import kr.co.wave.dto.approval.CardApprovalDTO;
import kr.co.wave.dto.card.CardDTO;
import kr.co.wave.dto.card.CardWithInfoDTO;
import kr.co.wave.entity.approval.CardApproval;
import kr.co.wave.entity.card.AnnualFee;
import kr.co.wave.entity.card.Benefit;
import kr.co.wave.entity.card.Card;
import kr.co.wave.repository.approval.CardApprovalRepository;
import kr.co.wave.repository.card.CardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardApprovalService {

    private final CardRepository cardRepository;
    private final CardApprovalRepository cardApprovalRepository;
    private final ModelMapper modelMapper; // Entity와 DTO를 변환해주는 객체

    // 카드 결재 요청 전체 가져오기
    @Transactional
    public Page<CardApprovalDTO> getCardApprovalAllBySearch(String searchType, String keyword, int page, int size) {
        // 검색어/타입 공백 처리
        String st = (searchType == null) ? "" : searchType.trim();
        String kw = (keyword == null) ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(page, size);

        Page<CardApprovalDTO> cardApprovalPage = cardApprovalRepository.findCardApprovalAllBySearchAll(st, kw, pageable);

        return cardApprovalPage;
    }

    // 카드 비활성화 승인
    @Transactional
    public void approval(int cardApprovalId) {
        Optional<CardApproval> OptionalCardApproval = cardApprovalRepository.findById(cardApprovalId);

        if(OptionalCardApproval.isPresent()){
            CardApproval cardApproval = OptionalCardApproval.get();
            cardApproval.toggleStatus("승인");

            Optional<Card> OptionalCard = cardRepository.findById(cardApproval.getCardId());

            if(OptionalCard.isPresent()){
                Card card = OptionalCard.get();
                card.toggleStatus("비활성");
            }
        }
    }

    // 카드 비활성화 반려
    @Transactional
    public void rejection(int cardApprovalId) {
        Optional<CardApproval> OptionalCardApproval = cardApprovalRepository.findById(cardApprovalId);

        if(OptionalCardApproval.isPresent()){
            CardApproval cardApproval = OptionalCardApproval.get();
            cardApproval.toggleStatus("반려");

            Optional<Card> OptionalCard = cardRepository.findById(cardApproval.getCardId());

            if(OptionalCard.isPresent()){
                Card card = OptionalCard.get();
                card.toggleStatus("활성");
            }
        }
    }

    @Transactional
    public CardApprovalDTO findCardApprovalPendingByCardId(int cardId) {
        return cardApprovalRepository.findCardApprovalPendingByCardId(cardId);
    }
}
