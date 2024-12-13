package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Integer> {
    @Query("SELECT q FROM Qna q WHERE q.parent IS NULL ORDER BY q.regDate DESC")
    List<Qna> findAllRootQna();
    List<Qna> findByParent(Qna parent);

    Page<Qna> findByParentIsNull(Pageable pageable);
    Page<Qna> findByAnswered(Pageable pageable, boolean answered);
}
