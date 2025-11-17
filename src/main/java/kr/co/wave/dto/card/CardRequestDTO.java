package kr.co.wave.dto.card;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CardRequestDTO {
    // 카드 등록 요청 받는 객체
    private String name;
    private String engName;
    private String type;
    private boolean isCompany;
    private String description;

    private List<String> category;
    private List<String> benefitType;
    private List<Integer> value;
    private List<String> unit;
    private List<Integer> limit;
    private List<String> benefitDescription;

    private List<String> annualFeeName;

    // 추가 필드 파일 업로드 ( 카드 이미지 + 백그라운드(옵션))
    private MultipartFile thumbnail; // 카드 이미지
    private MultipartFile background; // 카드 백그라운드 (필요시 사용) 일단 카드이미지만 써볼게요

    public void setIsCompany(boolean isCompany) {
        this.isCompany = isCompany;
    }
}
