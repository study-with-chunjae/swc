package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 이메일로 회원 찾기
    Optional<Member> findByEmail(String email);

    //친구(수진)
    @Query("SELECT m FROM Member m WHERE m.memberId LIKE %:keyword% OR m.name LIKE %:keyword%")
    List<Member> findById(String keyword, Pageable pageable);
    //친구(수진)
    Member findByMemberId(String memberId);

    //관리자(수진)
    Page<Member> findAll(Pageable pageable);
    Page<Member> findByMemberIdContaining(String memberId, Pageable pageable);
    Page<Member> findByNameContaining(String name, Pageable pageable);
    Page<Member> findByStatus(String status, Pageable pageable);
    Page<Member> findByMemberIdContainingOrNameContainingOrStatus(
            String memberId, String name, String status, Pageable pageable
    );
    // 스케줄러 사용
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.status = 'O' WHERE m.lastLoginAt < :cutoffDate  AND m.status = 'Y'")
    int updateStatusForMembers(LocalDateTime cutoffDate);

    // 수동변경
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.status = :status WHERE m.memberId = :memberId")
    int updateStatusByMemberId(String status, String memberId);
    //관리자(수진)
}
