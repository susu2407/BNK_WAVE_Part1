package kr.co.wave.entity.approval;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "TB_CARD_APPROVAL")
public class CardApproval {

    // 카드 결재 내역 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int approvalId;

    @Column
    private int cardId;

    @Column
    private String reason;

    // 요청자
    // 결재자
    @Column
    private String status; // 결재 상태 (대기, 승인, 반려)

    @Column(name="REQUESTED_AT")
    @CreationTimestamp
    private LocalDateTime requestedAt; // 요청 일시

    @Column(name="APPROVED_AT")
    @CreationTimestamp
    private LocalDateTime approvedAt; // 승인 일시

    public void toggleStatus(String status) {
        this.status = status;
    }
}
