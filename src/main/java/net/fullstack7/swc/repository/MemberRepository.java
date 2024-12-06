package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {


    //친구
    @Query("SELECT m FROM Member m WHERE m.memberId LIKE %:keyword% OR m.name LIKE %:keyword%")
    List<Member> findById(String keyword, Pageable pageable);
}
