package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
