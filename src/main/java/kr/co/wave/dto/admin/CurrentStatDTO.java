package kr.co.wave.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CurrentStatDTO {

    private final Integer currentNewProducts;
    private final Integer currentApprovalRequests;
    private final Integer currentIssueCompleted;
    private final Integer totalSellingProducts;

}