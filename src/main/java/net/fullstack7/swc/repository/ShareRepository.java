package net.fullstack7.swc.repository;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Integer> {
    Optional<Share> findByPostAndMember(Post post, Member member);

    // 아이디 전체 삭제 (한덕용 추가)
    void deleteAllByMember(Member member);
}
