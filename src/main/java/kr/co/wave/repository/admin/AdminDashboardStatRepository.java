package kr.co.wave.repository.admin;

import kr.co.wave.entity.admin.AdminDashboardStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdminDashboardStatRepository extends JpaRepository<AdminDashboardStat, Integer> {

    // 1. 핵심 지표(Current Stats)용: STAT_DATE가 가장 최신인 데이터 1건을 가져옴
    Optional<AdminDashboardStat> findFirstByOrderByStatDateDesc();

    // 2. Stacked Column Chart용: STAT-DATE를 기준으로 보두 가져와서 최신순으로 나열
    List<AdminDashboardStat> findAllByOrderByStatDateAsc();

    // 3. Doughnut Chart 용: 특정 월의 데이터를 가져옴
    // 3-1 최신 월 데이터 조회: 1 활용
    // ３－2. 특정 월 데이터 조회 (선택된 월): 정확히 일치하는 월의 데이터 1건을 가져옴
    Optional<AdminDashboardStat> findByStatDate(LocalDate statDate);

    // ３－3. 이전 월 데이터 조회: 현재 월(statDate)보다 '이전'이면서 가장 '최신' 월 1건
    Optional<AdminDashboardStat> findFirstByStatDateBeforeOrderByStatDateDesc(LocalDate statDate);

    // ３－4. 다음 월 데이터 조회: 현재 월(statDate)보다 '이후'이면서 가장 '오래된' 월 1건
    Optional<AdminDashboardStat> findFirstByStatDateAfterOrderByStatDateAsc(LocalDate statDate);
}
