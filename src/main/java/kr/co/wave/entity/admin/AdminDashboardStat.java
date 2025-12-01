package kr.co.wave.entity.admin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "TB_ADMIN_DASHBOARD_STATS")
public class AdminDashboardStat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_stats_seq_gen")  // admin_stats (테이블) + seq (시퀀스) + gen (생성기)
    @SequenceGenerator(name = "admin_stats_seq_gen", sequenceName = "TB_ADMIN_DASHBOARD_STATS_SEQ", allocationSize = 1)
    @Column(name = "STAT_ID")
    private int statId;

    @Column(name = "STAT_DATE")
    private LocalDate statDate;                 // 통계 기준 월

    // 핵심 지표
    @Column(name = "CURRENT_NEW_PRODUCTS")
    private Integer currentNewProducts;         // 이번 달 신규 상품 등록수

    @Column(name = "CURRENT_APPROVAL_REQUESTS")
    private Integer currentApprovalRequests;    // 승인 요청 건수

    @Column(name = "CURRENT_ISSUE_COMPLETED")
    private Integer currentIssueCompleted;      // 발급 완료 건수

    @Column(name = "TOTAL_SELLING_PRODUCTS")
    private Integer totalSellingProducts;       // 판매 중인 상품 수

    // Stacked Chart 데이터
    @Column(name = "ISSUE_MOBILE_COUNT")
    private Integer issueMobileCount;           // 월별 발급 추이(모바일)

    @Column(name = "ISSUE_WEB_COUNT")
    private Integer issueWebCount;              // 월별 발급 추이(웹)

    @Column(name = "ISSUE_OFFLINE_COUNT")
    private Integer issueOfflineCount;          // 월별 발급 추이(오프라인)

    // Doughnut Chart 데이터
    @Column(name = "PROD_CORPORATE_CHECK")
    private Integer prodCorporateCheck;         // 상품별 발급 추이(기업체크)

    @Column(name = "PROD_CORPORATE_CREDIT")
    private Integer prodCorporateCredit;        // 상품별 발급 추이(기업신용)

    @Column(name = "PROD_PERSONAL_CHECK")
    private Integer prodPersonalCheck;          // 상품별 발급 추이(개인체크)

    @Column(name = "PROD_PERSONAL_CREDIT")
    private Integer prodPersonalCredit;         // 상품별 발급 추이(개인신용)

    @Column(name = "PROD_PREMIUM")
    private Integer prodPremium;                // 상품별 발급 추이(프리미엄)


}
