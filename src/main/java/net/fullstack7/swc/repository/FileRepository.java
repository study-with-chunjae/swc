package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer> {
}
