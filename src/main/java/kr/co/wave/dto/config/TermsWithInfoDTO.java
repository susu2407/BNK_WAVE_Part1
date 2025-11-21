package kr.co.wave.dto.config;

import kr.co.wave.entity.config.Terms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsWithInfoDTO {
    private TermsRepositoryDTO term;
    private String approvalStatus;
}
