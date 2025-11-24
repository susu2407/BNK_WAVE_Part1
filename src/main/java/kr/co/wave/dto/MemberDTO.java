package kr.co.wave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private String memId;
    private String password;
    private String name;
    private String role;
    private LocalDateTime createdAt;
    private String status;
}
