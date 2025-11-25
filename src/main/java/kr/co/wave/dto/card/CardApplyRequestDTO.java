package kr.co.wave.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplyRequestDTO {
    // 사용자한테 입력 받을 정보 - 로그인 / 비로그인 상태던 일단 카드신청은 됨

    // 카드 정보
    private int cardId;

    // 사용자 입력 정보
    private String name;           // 한글 이름
    private String lastNameEn;     // 영문 성
    private String firstNameEn;    // 영문 이름
    private String rrn;            // 주민번호
    private String phone;          // 휴대폰
    private String email;          // 이메일

    // 추가 필드 [step5]
    private String job; // 직업 구분
    private String riskJob; // 고위험직업군 확인
    private String fundSource; // 자금의 원천
    private String purpose; // 거래의 목적

    // Step 6 추가 필드
    private String postpaidTransit; // 후불교통 기능 신청 여부 ('Y' 또는 'N')
    private String overseasUse;    // 해외 결제 여부 ('dual' 또는 'domestic')


    // Step 7 추가 필드

    private String accountBank; // 은행명
    private String accountNumber; // 계좌번호
    private Integer accountVerified; // 계좌인증 여부 0,1 (0 = 미인증 , 1 = 인증)

    // Step 8 추가 필드

    private String pin; // 카드 비밀번호 ( DB 저장 전 암호화 진행)


    // Step 9 추가필드
    private String zip; // 우편번호
    private String addr1; // 기본 주소
    private String addr2; // 상세 주소


    // 결제 정보 (나중에 받을 예정이면 null)
    private String accountId;      // 계좌번호
}
