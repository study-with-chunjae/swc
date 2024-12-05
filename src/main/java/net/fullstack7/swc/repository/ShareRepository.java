package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Integer> {
}
