package kr.co.wave.repository.approval;

import jakarta.persistence.Column;
import kr.co.wave.dto.approval.CardApprovalDTO;
import kr.co.wave.dto.card.CardDTO;
import kr.co.wave.entity.approval.CardApproval;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public interface CardApprovalRepository extends JpaRepository<CardApproval, Integer> {

    @Query(
            value = """
                      select new kr.co.wave.dto.approval.CardApprovalDTO(
                         a.approvalId, a.cardId, a.reason, a.status, a.requestedAt, a.approvedAt
                      )
                      from CardApproval a
                      where
                        (:keyword is null or :keyword = '')
                        or (
                          ( :searchType is null or :searchType = '' ) and (
                            lower(a.status)    like lower(concat('%', :keyword, '%')) 
                          )
                        )
                        or (:searchType = 'status'    and lower(a.status)    like lower(concat('%', :keyword, '%')))
                      order by a.approvalId desc
                    """
    )
    Page<CardApprovalDTO> findCardApprovalAllBySearchAll(@Param("searchType") String searchType,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);


    @Query("""
        select new kr.co.wave.dto.approval.CardApprovalDTO(
            a.approvalId, a.cardId, a.reason, a.status, a.requestedAt, a.approvedAt
        )
        from CardApproval a
        where a.cardId = :cardId and a.status = '대기'
       """)
    // 카드번호를 기반으로 상태가 '대기'인 것을 불러옴
    CardApprovalDTO findCardApprovalPendingByCardId(@Param("cardId") int cardId);

}