package kr.co.wave.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailsResponse {
    private String cardName;
    private String cardType;
    private String annualFee;
    private String benefits;
    private String description;
    private String error;
    private String detail;

    public static CardDetailsResponse fromCardInfo(CardWithInfoDTO cardInfo) {
        // 연회비 정보 포맷팅
        String annualFeeStr = cardInfo.getAnnualFeeList().stream()
                .map(fee -> fee.getAnnualName())
                .collect(Collectors.joining(", "));

        // 혜택 정보 포맷팅
        String benefitsStr = cardInfo.getBenefitList().stream()
                .map(benefit -> String.format("%s: %s %d%s (한도: %d원)",
                        benefit.getBenefitCategory(),
                        benefit.getBenefitType(),
                        benefit.getValue(),
                        benefit.getUnit(),
                        benefit.getLimit()))
                .collect(Collectors.joining(", "));

        return CardDetailsResponse.builder()
                .cardName(cardInfo.getCard().getName())
                .cardType(cardInfo.getCard().getType())
                .annualFee(annualFeeStr.isEmpty() ? "정보 없음" : annualFeeStr)
                .benefits(benefitsStr.isEmpty() ? "정보 없음" : benefitsStr)
                .description(cardInfo.getCard().getDescription())
                .build();
    }
}