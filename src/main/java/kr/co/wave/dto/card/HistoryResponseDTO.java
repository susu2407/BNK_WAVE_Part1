package kr.co.wave.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResponseDTO {
    // 1128 박효빈 생성 -> my page- RecentHistory 화면에 뿌려줄 데이터

    private String branchName; // 가맹점명
    private String paymentType; // 이용 구분 일시불/...
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDateTime paymentAt; // 결재일시
    private int historyId;

    // View에 필요한 가공된 필드 (Service에서 계산하여 주입)
    private String cardName;          // 카드명
    private String amountFormatted;   // 포맷팅된 금액 (예: 5,100원)
    private String metaInfo;          // 카드명 · 날짜 · 시간 · 구분 조합 문자열
}

