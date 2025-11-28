package kr.co.wave.repository.card;

import kr.co.wave.dto.card.CardDTO;
import kr.co.wave.entity.card.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {

    @Query(
            value = """
                      select new kr.co.wave.dto.card.CardDTO(
                         c.cardId, c.name, c.engName, c.type, c.isCompany, c.description, c.thumbnail, c.background, c.status, c.createdAt, c.updatedAt
                      )
                      from Card c
                      where
                        (:keyword is null or :keyword = '')
                        or (
                          ( :searchType is null or :searchType = '' ) and (
                            lower(c.name)    like lower(concat('%', :keyword, '%')) or
                            lower(c.engName)  like lower(concat('%', :keyword, '%')) or
                            lower(c.type) like lower(concat('%', :keyword, '%'))
                          )
                        )
                        or (:searchType = 'name'    and lower(c.name)    like lower(concat('%', :keyword, '%')))
                        or (:searchType = 'engName'  and lower(c.engName)  like lower(concat('%', :keyword, '%')))
                        or (:searchType = 'type' and lower(c.type) like lower(concat('%', :keyword, '%')))
                      order by c.cardId desc
                    """
    )
    Page<CardDTO> findCardAllBySearch(@Param("searchType") String searchType,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);
    @Query("""
    SELECT new kr.co.wave.dto.card.CardDTO(
        c.cardId, c.name, c.engName, c.type, c.isCompany,
        c.description, c.thumbnail, c.background, c.status,
        c.createdAt, c.updatedAt
    )
    FROM Card c
    WHERE c.status = '활성'
      AND (
            :keyword = '' 
            OR c.name LIKE %:keyword%
            OR c.engName LIKE %:keyword%
            OR c.type LIKE %:keyword%
            OR c.description LIKE %:keyword%
            OR c.cardId IN (
                SELECT b.card.cardId FROM Benefit b
                WHERE b.benefitCategory LIKE %:keyword%
            )
        )
    ORDER BY c.cardId DESC
    """)
    Page<CardDTO> findCardAllBySearch2(@Param("searchType") String searchType,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    // 신용카드만
    @Query("SELECT c FROM Card c WHERE c.type = '신용'")
    List<Card> findByTypeCredit();

    // 체크카드만
    @Query("SELECT c FROM Card c WHERE c.type = '체크'")
    List<Card> findByTypeCheck();


    // 11.28 박효빈 추가 (랜덤 4개 쿼리 추가 - 추천 카드 뽑기 어려워서 일단은 메인 페이지 정상화를 위함)
    @Query("""
SELECT new kr.co.wave.dto.card.CardDTO(
    c.cardId, c.name, c.engName, c.type, c.isCompany,
    c.description, c.thumbnail, c.background, c.status,
    c.createdAt, c.updatedAt
)
FROM Card c
WHERE c.status = '활성'
ORDER BY function('DBMS_RANDOM.VALUE')
""")
    List<CardDTO> findRandomCardsWithCustomSort(@Param("status") String status, Pageable pageable);
}
