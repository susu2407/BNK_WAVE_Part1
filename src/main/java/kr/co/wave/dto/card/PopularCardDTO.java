package kr.co.wave.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 11.27 박효빈 - 메인화면에 인기카드 DTO 생성
public class PopularCardDTO {

    private int cardId;
    private String name;
    private String thumbnail;
    private Long cardCount;
}
