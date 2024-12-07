package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PageDTO;
import net.fullstack7.swc.repository.search.PostSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer>, PostSearch {
    List<Post> findByMemberAndTodayTypeAndCreatedAtBetweenAndDisplayAtBeforeAndDisplayEndAfter(
            Member member, Integer todayType, LocalDateTime createdAt,
            LocalDateTime createdAt2, LocalDateTime displayAt, LocalDateTime displayEnd
    );
    Page<Post> searchAndSort(Pageable pageable, String searchField, String searchValue, String sortField,
                             String sortDirection, String searchDateBegin, String searchDateEnd, String memberId);
}
