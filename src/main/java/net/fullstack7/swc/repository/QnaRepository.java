package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Integer> {
}
