package kr.co.wave.repository.member;

import kr.co.wave.entity.member.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
