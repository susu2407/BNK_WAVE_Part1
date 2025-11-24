package kr.co.wave.repository.member;

import kr.co.wave.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemId(String MemId);
    boolean existsByMemId(String MemId);

    @Query(
            value = """
              select new kr.co.wave.dto.MemberDTO(
                 m.memId, m.password, m.name, m.role, m.createdAt, m.status
              )
              from Member m
              where lower(m.role) like 'admin%'
                and (
                    (:keyword is null or :keyword = '')
                    or (
                      ( :searchType is null or :searchType = '' ) and (
                        lower(m.name) like lower(concat('%', :keyword, '%')) or
                        lower(m.role) like lower(concat('%', :keyword, '%'))
                      )
                    )
                    or (:searchType = 'name' and lower(m.name) like lower(concat('%', :keyword, '%')))
                    or (:searchType = 'role' and lower(m.role) like lower(concat('%', :keyword, '%')))
                )
              order by m.memId desc
            """
    )
    Page<Member> findAdminAll(@Param("searchType") String searchType,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}

