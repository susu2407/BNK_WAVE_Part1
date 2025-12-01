package kr.co.wave.controller.my;

import kr.co.wave.dto.card.CardWithInfoDTO;
import kr.co.wave.entity.card.Card;
import kr.co.wave.service.card.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final CardService cardService;
    private final RestTemplate restTemplate = new RestTemplate();

    // 신용 카드 정보 반환
    public ResponseEntity<List<CardWithInfoDTO>> getCredit() {
        return ResponseEntity.ok(cardService.findCardAllWhereCredit());
    }

    // 체크 카드 정보 반환
    public ResponseEntity<List<CardWithInfoDTO>> getCheck() {
        return ResponseEntity.ok(cardService.findCardAllWhereCheck());
    }

    // FastAPI 서버에 요청 보내고 답변 받기
    @PostMapping("/call-fastapi")
    public String callFastApi(@RequestBody String json) {

        String url = "http://127.0.0.1:8000/ask2";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        return restTemplate.postForObject(url, entity, String.class);
    }
}
