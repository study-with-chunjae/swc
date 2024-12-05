package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Integer> {
}
