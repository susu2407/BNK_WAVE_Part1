package kr.co.wave.entity.card;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TB_ACCOUNT")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ACCOUNT_ID")
    private Integer accountId;

    @Column(name="MEM_ID")
    private String memId;

    @Column(name="ACCOUNT_BANK")
    private String accountBank;

    @Column(name="ACCOUNT_NUMBER")
    private String accountNumber;

    @Column(name="ACCOUNT_VERIFIED")
    private Integer accountVerified;

    @Column(name="PIN")
    private String pin;
}
