package kr.co.wave.service.approval;

import jakarta.transaction.Transactional;
import kr.co.wave.entity.approval.CardApproval;
import kr.co.wave.entity.card.Card;
import kr.co.wave.repository.approval.CardApprovalRepository;
import kr.co.wave.repository.card.CardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
}
