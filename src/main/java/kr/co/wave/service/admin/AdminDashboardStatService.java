package kr.co.wave.service.admin;

import kr.co.wave.dto.admin.CurrentStatDTO;
import kr.co.wave.dto.admin.DoughnutChartDTO;
import kr.co.wave.dto.admin.StackedChartDTO;
import kr.co.wave.entity.admin.AdminDashboardStat;
import kr.co.wave.repository.admin.AdminDashboardStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardStatService {

    /*
    * 1.
    * 이번달 신규 상품 등록 수 불러오기
    * 승인 요청 건수 불러오기
    * 발급 완료 건수 불러오기
    * 판매 중인 상품 수 불러오기
    *
    * 2.
    * 월별 발급 추이-모바일, 웹, 오프라인-를 각각 불러와서,
    * 한 바에 차례로 올리고(그룹?),
    * 월별로 차트 6개월 정도 나타내기
    *
    * 3.
    * 상품별 발급 추이-기업신용,기업체크,개인신용,개인체크,프리미엄- 각각 값 불러오기
    * 불러온 값은 도넛 차트에 연결하기
    * 데이터 없는 기간으로 넘어가지 않도록 하기
    *
    * */

    private final AdminDashboardStatRepository statsRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    // 1. 핵심 지표 (Current Status) 조회 및 가공
    public CurrentStatDTO getCurrentStats() {

        AdminDashboardStat latestStat = statsRepository.findFirstByOrderByStatDateDesc()
                .orElseThrow(() -> new NoSuchElementException("최신 통계 데이터가 존재하지 않습니다."));

        return CurrentStatDTO.builder()
                .currentNewProducts(latestStat.getCurrentNewProducts())
                .currentApprovalRequests(latestStat.getCurrentApprovalRequests())
                .currentIssueCompleted(latestStat.getCurrentIssueCompleted())
                .totalSellingProducts(latestStat.getTotalSellingProducts())
                .build();

    }


    // 2. Stacked Column Chart 데이터 조회 및 가공
    public StackedChartDTO getStackedChart() {

        // 데이터 가지고 오기 -> 웹/모바일/오프라인 을 뽑아내어 각각 담기 -> stacked 에 하나씩 부여? -> 월별 그룹핑? -> 월별 나열?
        List<AdminDashboardStat> stats = statsRepository.findAllByOrderByStatDateAsc();

        if (stats.isEmpty()) {
            return StackedChartDTO.builder()
                    .labels(Collections.emptyList())
                    .datasets(Collections.emptyList())
                    .build();
        }

        // + 리스트의 개수 확인
        int size = stats.size();
        int limit = 6;

        // + 데이터가 6개보다 많으면, 최신 6개만 자릅니다.
        // stats.subList(시작 인덱스, 끝 인덱스);
        if (size > limit) {
            stats = stats.subList(size - limit, size);
        }

        // x축 레이블 (월별 yyyy-mm)구성
        List<String> labels = stats.stream()
                .map(stat -> stat.getStatDate().format(MONTH_FORMATTER)).toList();

        // 데이터 구성 - Chart.js 포멧에 맞춤
        List<Map<String, Object>> datasets = new ArrayList<>();

        // 채널1 : 모바일 발급 건수
        datasets.add(Map.of("label", "모바일",
                "data", stats.stream().map(AdminDashboardStat::getIssueMobileCount).toList()));
        // 채널2 : 웹 발급 건수
        datasets.add(Map.of("label", "웹",
                "data", stats.stream().map(AdminDashboardStat::getIssueWebCount).toList()));
        // 채널3 : 오프라인 발급 건수
        datasets.add(Map.of("label", "오프라인",
                "data", stats.stream().map(AdminDashboardStat::getIssueOfflineCount).toList()));

        return StackedChartDTO.builder()
                .labels(labels)
                .datasets(datasets)
                .build();
    }
    // 3. Doughnut Chart 데이터 조회 및 가공

    // 상품 분류 레이블은 고정값이므로 상수로 정의
    private static final List<String> DOUGHNUT_LABELS = List.of(
            "기업 체크카드", "기업 신용카드", "개인 체크카드", "개인 신용카드", "프리미엄 카드"
    );

    // 3. 메인

    // 로드 시 최신 월의 Doughnut Chart 데이터를 반환합니다.
    public DoughnutChartDTO getLatestDoughnutChartData() {
        // Repository 3-1 메소드 사용
        Optional<AdminDashboardStat> latestStatOpt = statsRepository.findFirstByOrderByStatDateDesc();

        AdminDashboardStat stat = latestStatOpt.orElseThrow(() ->
                new NoSuchElementException("최신 통계 데이터가 없어 Doughnut Chart를 로드할 수 없습니다.")
        );

        return mapToDoughnutChartDTO(stat);
    }


    // 특정 월의 Doughnut Chart 데이터를 반환합니다. (월 이동 기능에서 사용)
    public DoughnutChartDTO getDoughnutChartDataByMonth(LocalDate statDate) {
        // Repository 3-2 메소드 사용
        Optional<AdminDashboardStat> statOpt = statsRepository.findByStatDate(statDate);

        AdminDashboardStat stat = statOpt.orElseThrow(() ->
                new NoSuchElementException(statDate.format(MONTH_FORMATTER) + " 월의 통계 데이터가 없습니다.")
        );

        return mapToDoughnutChartDTO(stat);
    }

    // 현재 월보다 이전 월의 데이터를 찾아 반환
    public DoughnutChartDTO getPreviousDoughnutChartData(LocalDate currentDate) {
        // Repository 3-3 메소드 사용
        Optional<AdminDashboardStat> prevStatOpt =
                statsRepository.findFirstByStatDateBeforeOrderByStatDateDesc(currentDate);

        AdminDashboardStat stat = prevStatOpt.orElseThrow(() ->
                new NoSuchElementException("이전 월의 통계 데이터가 없습니다.")
        );

        return mapToDoughnutChartDTO(stat);
    }

    // 현재 월보다 다음 월의 데이터를 찾아 반환
    public DoughnutChartDTO getNextDoughnutChartData(LocalDate currentDate) {
        // Repository 3-4 메소드 사용
        Optional<AdminDashboardStat> nextStatOpt =
                statsRepository.findFirstByStatDateAfterOrderByStatDateAsc(currentDate);

        AdminDashboardStat stat = nextStatOpt.orElseThrow(() ->
                new NoSuchElementException("다음 월의 통계 데이터가 없습니다.")
        );

        return mapToDoughnutChartDTO(stat);
    }

    // ----------------------------------------------------
    // 공통 매핑 로직 (Helper Method)
    // ----------------------------------------------------
    private DoughnutChartDTO mapToDoughnutChartDTO(AdminDashboardStat stat) {

        // 1. Entity 필드의 값을 순서대로 List로 구성
        List<Number> data = List.of(
                stat.getProdCorporateCheck() != null ? stat.getProdCorporateCheck() : 0,
                stat.getProdCorporateCredit() != null ? stat.getProdCorporateCredit() : 0,
                stat.getProdPersonalCheck() != null ? stat.getProdPersonalCheck() : 0,
                stat.getProdPersonalCredit() != null ? stat.getProdPersonalCredit() : 0,
                stat.getProdPremium() != null ? stat.getProdPremium() : 0
        );

        // 인접 월 데이터 존재 여부 확인
        boolean hasPrevious = statsRepository.findFirstByStatDateBeforeOrderByStatDateDesc(stat.getStatDate()).isPresent();
        boolean hasNext = statsRepository.findFirstByStatDateAfterOrderByStatDateAsc(stat.getStatDate()).isPresent();

        // 2. DTO 생성 및 반환
        return DoughnutChartDTO.builder()
                .currentMonth(stat.getStatDate().format(MONTH_FORMATTER)) // 현재 표시 월
                .labels(DOUGHNUT_LABELS) // 고정된 상품 레이블
                .data(data)
                .hasPreviousMonth(hasPrevious)  // 다음 달 존재 여부 체크, 추가
                .hasNextMonth(hasNext)
                .build();
    }




}
