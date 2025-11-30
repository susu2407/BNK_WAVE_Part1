package kr.co.wave.dto.admin;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class DoughnutChartDTO {

    private final String currentMonth; // 현재 표시 중인 월 (YYYY-MM)
    private final List<String> labels; // 상품 분류명
    private final List<Number> data;   // 각 상품별 발급 건수

    // 이전/다음 월 데이터 존재 여부 확인용
    private final boolean hasPreviousMonth;
    private final boolean hasNextMonth;

}
