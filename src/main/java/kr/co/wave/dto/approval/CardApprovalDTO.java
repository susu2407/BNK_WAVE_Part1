package kr.co.wave.dto.approval;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApprovalDTO {

    private int ApprovalId;

    private int cardId;

    private String reason;

    private String status; // 결재 상태 (대기, 승인, 반려)

    private LocalDateTime requestedAt; // 요청 일시

    private LocalDateTime approvedAt; // 승인 일시
}
