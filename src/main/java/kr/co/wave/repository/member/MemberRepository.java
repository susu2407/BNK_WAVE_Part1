package kr.co.wave.repository.member;

import kr.co.wave.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemId(String MemId);
    boolean existsByMemId(String MemId);
}
