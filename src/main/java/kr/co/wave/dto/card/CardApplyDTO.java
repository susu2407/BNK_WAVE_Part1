package kr.co.wave.dto.card;

import java.time.LocalDateTime;

public class CardApplyDTO {

    // 회원 ID (로그인한 사용자 기준, 서버에서 세팅)
    private String memId;

    // 사용자가 선택한 카드 ID
    private int cardId;

    // 신청자 정보
    private String name;
    private String firstName;
    private String lastName;
    private String rrn; // 주민등록번호
    private String phoneNumber;
    private String email;
    private boolean termsAgree; // 약관 동의 여부

    // 신청 상태 (진행중, 완료 등)
    private String status = "진행중";

    // 신청일
    private LocalDateTime createdAt;
}
