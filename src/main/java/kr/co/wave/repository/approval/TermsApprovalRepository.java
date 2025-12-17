package kr.co.wave.repository.approval;

import kr.co.wave.dto.approval.CardApprovalDTO;
import kr.co.wave.dto.approval.TermsApprovalDTO;
import kr.co.wave.entity.approval.CardApproval;
import kr.co.wave.entity.approval.TermsApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TermsApprovalRepository extends JpaRepository<TermsApproval, Integer> {

    @Query(
            value = """
                      select new kr.co.wave.dto.approval.TermsApprovalDTO(
                         ta.termsApprovalId, ta.termsId, ta.title, ta.reason, ta.status, ta.requestedAt, ta.approvedAt
                      )
                      from TermsApproval ta
                      where
                        (:keyword is null or :keyword = '')
                        or (
                          ( :searchType is null or :searchType = '' ) and (
                            lower(ta.status)    like lower(concat('%', :keyword, '%')) 
                          )
                        )
                        or (:searchType = 'status'    and lower(ta.status)    like lower(concat('%', :keyword, '%')))
                      order by ta.termsApprovalId desc
                    """
    )
    Page<TermsApprovalDTO> findTermsApprovalAllBySearchAll(@Param("searchType") String searchType,
                                                          @Param("keyword") String keyword,
                                                          Pageable pageable);


    @Query("""
        select new kr.co.wave.dto.approval.TermsApprovalDTO(
            ta.termsApprovalId, ta.termsId, ta.title, ta.reason, ta.status, ta.requestedAt, ta.approvedAt
        )
        from TermsApproval ta
        where ta.termsId = :termsId and ta.status = '대기'
       """)
    // 상태가 '대기'인 것을 불러옴
    TermsApprovalDTO findTermsApprovalPendingByTermsId(@Param("termsId") int termsId);

}