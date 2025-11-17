package kr.co.wave.repository.approval;

import kr.co.wave.entity.approval.CardApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardApprovalRepository extends JpaRepository<CardApproval, Integer> {

}