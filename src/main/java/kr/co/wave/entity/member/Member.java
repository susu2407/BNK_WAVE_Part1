package kr.co.wave.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "TB_MEMBER")
public class Member {

    // 멤버(유저, 사용자) Entity 정의
    // 각 Entity마다 Repository, Service 정의해두면 편함.
    // Entity의 정석은 일관성을 유지하기 위해 Setter를 정의하지 않음. 하지만 데이터를 변경해야 할 일이 있기 때문에 같은 형태의 DTO도 선언.

    @Id
    @Column(name = "MEM_ID")
    private String memId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FIRST_NAME_EN")
    private String firstNameEn;  // 영문 이름

    @Column(name = "LAST_NAME_EN")
    private String lastNameEn;   // 영문 성

    @Column(name = "ROLE")
    private String role;

    @Column(name = "ADDRESS")
    private String address;

    @Column (name = "EMAIL")
    private String email;

    @Column (name = "RRN")
    private String rrn;

    @Column (name = "DEADDRESS")
    private String deaddress;

    @Column (name = "ZIP")
    private String zip;

    @CreationTimestamp
    @Column(name="CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name="STATUS")
    private String status;

    // 카드 신청 단계에서 수집된 정보만 업데이트하는 메서드
    public void updateApplicationInfo(
            String name,
            String firstNameEn,
            String lastNameEn,
            String email,
            String rrn,
            String zip,
            String address,
            String deaddress
    ) {
        this.name = name;
        this.firstNameEn = firstNameEn;
        this.lastNameEn = lastNameEn;
        this.email = email;
        this.rrn = rrn;
        this.zip = zip;
        this.address = address;
        this.deaddress = deaddress;
        this.status = "활성"; // 상태도 이 시점에 활성으로 업데이트
        // role은 업데이트하지 않습니다. (기존 role 유지)
    }

}
