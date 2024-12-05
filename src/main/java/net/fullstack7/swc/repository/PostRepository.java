package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
