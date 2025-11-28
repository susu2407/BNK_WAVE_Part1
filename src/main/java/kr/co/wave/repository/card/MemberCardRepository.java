package kr.co.wave.repository.card;


import kr.co.wave.dto.card.PopularCardDTO;
import kr.co.wave.entity.card.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MemberCardRepository extends JpaRepository<MemberCard, Integer> {



    @Query(value = """
        SELECT new kr.co.wave.dto.card.PopularCardDTO(
                           mc.cardId, c.name, c.thumbnail, COUNT(mc.cardId)
                               )
                       
           FROM MemberCard mc
        JOIN Card c ON mc.cardId = c.cardId
        GROUP BY mc.cardId, c.name, c.thumbnail
        ORDER BY COUNT(mc.cardId) DESC
    """)
    List<PopularCardDTO> findPopularCards();
}