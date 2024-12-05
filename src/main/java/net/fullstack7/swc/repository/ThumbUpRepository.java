package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.ThumbUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbUpRepository extends JpaRepository<ThumbUp, Integer> {
}
