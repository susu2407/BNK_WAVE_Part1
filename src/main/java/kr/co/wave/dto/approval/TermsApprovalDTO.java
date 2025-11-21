package kr.co.wave.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsApprovalDTO {

    private int termsApprovalId;

    private int termsId;

    private String reason;

    private String status; // 결재 상태 (대기, 승인, 반려)

    private LocalDateTime requestedAt; // 요청 일시

    private LocalDateTime approvedAt; // 승인 일시
}
