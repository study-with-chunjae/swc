package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.ThumbUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThumbUpRepository extends JpaRepository<ThumbUp, Integer> {
    Optional<ThumbUp> findByPostAndMember(Post post, Member member);
}
