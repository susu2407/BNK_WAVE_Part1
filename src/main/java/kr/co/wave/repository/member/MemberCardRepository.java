package kr.co.wave.repository.member;

import kr.co.wave.entity.card.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCardRepository extends JpaRepository<MemberCard, Integer> {}
