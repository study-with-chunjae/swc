package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
}
