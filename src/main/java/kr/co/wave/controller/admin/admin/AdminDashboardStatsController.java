package kr.co.wave.controller.admin.admin;

import kr.co.wave.dto.admin.CurrentStatDTO;
import kr.co.wave.dto.admin.DoughnutChartDTO;
import kr.co.wave.dto.admin.StackedChartDTO;
import kr.co.wave.service.admin.AdminDashboardStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/dashboard") // 기본 경로 설정???
@RequiredArgsConstructor
public class AdminDashboardStatsController {

    private final AdminDashboardStatService statsService;

    // 1. 핵심 지표
    @GetMapping("/current")
    public ResponseEntity<CurrentStatDTO> getCurrenStats() {

        return ResponseEntity.ok(statsService.getCurrentStats());
    }

    // 2. 월별 발급 추이 Stacked Chart
    @GetMapping("/chart/stacked-column")
    public ResponseEntity<StackedChartDTO> getStackedChartData() {

        return ResponseEntity.ok(statsService.getStackedChart());
    }

    // 3. 월 상품별 발급 추이 Doughnut Chart
    @GetMapping("/chart/doughnut")
    public ResponseEntity<DoughnutChartDTO> getDoughnutChartData(@RequestParam(value = "month", required = false) String month,
                                                                @RequestParam(value = "direction", required = false) String direction) {
        DoughnutChartDTO data;

        // --- 1. 요청 파라미터에 따라 분기 ---
        if (month == null) {
            // 초기 로드: month 파라미터가 없으면 최신 월 데이터 반환
            data = statsService.getLatestDoughnutChartData();

        } else {
            // 특정 월 요청 (YYYY-MM 형식 문자열을 LocalDate의 YYYY-MM-01로 변환)
            // ex: "2024-11" -> LocalDate.of(2024, 11, 1)
            LocalDate statDate = LocalDate.parse(month + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if ("prev".equals(direction)) {
                // 이전 월 버튼 클릭
                data = statsService.getPreviousDoughnutChartData(statDate);
            } else if ("next".equals(direction)) {
                // 다음 월 버튼 클릭
                data = statsService.getNextDoughnutChartData(statDate);
            } else {
                // 특정 월 데이터 조회 (월 이동 기능 외의 특정 월 요청)
                data = statsService.getDoughnutChartDataByMonth(statDate);
            }
        }

        // --- 2. 응답 반환 ---
        // Service에서 NoSuchElementException이 발생하면 Spring이 500 또는 적절한 예외 응답으로 처리합니다.
        return ResponseEntity.ok(data);
    }

}
