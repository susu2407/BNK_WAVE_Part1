package kr.co.wave.entity.member;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="TB_ADDRESS")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ADDRESS_ID")
    private int addressId;

    @Column(name="MEM_ID")
    private String memId;

    @Column(name="ZIP")
    private String zip;

    @Column(name="ADDR1")
    private String addr1;

    @Column(name="ADDR2")
    private String addr2;
}
