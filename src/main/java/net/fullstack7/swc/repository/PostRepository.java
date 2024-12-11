package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.repository.search.PostSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer>, PostSearch {
    List<Post> findByMemberAndTodayTypeAndCreatedAtBetweenAndDisplayAtBeforeAndDisplayEndAfter(
            Member member, Integer todayType, LocalDateTime createdAt,
            LocalDateTime createdAt2, LocalDateTime displayAt, LocalDateTime displayEnd
    );
    List<Post> findByMemberAndTodayTypeAndDisplayAtAfterAndDisplayEndBefore(
            Member member, Integer todayType, LocalDateTime now, LocalDateTime now2
    );
    Page<Post> searchAndSort(Pageable pageable, String searchField, String searchValue, String sortField,
                             String sortDirection, String searchDateBegin, String searchDateEnd, String memberId);
    Page<Post> searchAndSortMyShare(Pageable pageable, String searchField, String searchValue, String sortField,
                             String sortDirection, String searchDateBegin, String searchDateEnd, String memberId);
    Page<Post> searchAndSortOthersShare(Pageable pageable, String searchField, String searchValue, String sortField,
                                        String sortDirection, String searchDateBegin, String searchDateEnd, String memberId);
    Page<Post> searchAndSortShare(Pageable pageable, String searchField, String searchValue, String sortField,
                                        String sortDirection, String searchDateBegin, String searchDateEnd, String memberId, String type);
    @Query(
            value = """
                        SELECT p
                        FROM Post p
                        LEFT JOIN FETCH Share s ON p.postId = s.post.postId
                        LEFT JOIN FETCH ThumbUp t ON p.postId = t.post.postId
                        WHERE p.postId = :postId
                    """
    )
    Post viewPost(Integer postId);

}
