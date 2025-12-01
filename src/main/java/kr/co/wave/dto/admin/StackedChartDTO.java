package kr.co.wave.dto.admin;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class StackedChartDTO {

    private final List<String> labels;                  // X축 레이블 (예: ["2025-09", "2025-10", "2025-11"])

    private final List<Map<String, Object>> datasets;   // 데이터 시리즈: [{label: "모바일", data: [4000, 4500, 5500]}, ...]

}
