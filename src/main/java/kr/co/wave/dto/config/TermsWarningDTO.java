package kr.co.wave.dto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsWarningDTO {
    private int termsId;

    private String title; // 제목

    private String version;	// 버전 (예: v1.0, v1.1)

    private String termStatus;
}
