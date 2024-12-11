package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {
  Optional<MemberProfile> findByMember(Member member);
  void deleteAllByMember(Member member);
}
