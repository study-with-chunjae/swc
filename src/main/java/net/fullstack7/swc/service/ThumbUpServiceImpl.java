package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.ThumbUp;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.repository.ThumbUpRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ThumbUpServiceImpl implements ThumbUpServiceIf{
    private final ThumbUpRepository thumbUpRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    public ThumbUp addThumbUp(Integer postId, String memberId){
        try{
            Post post = postRepository.findById(postId).orElseThrow(()->new IllegalArgumentException("post not found"));
            Member member = memberRepository.findById(memberId).orElseThrow(()->new IllegalArgumentException("member not found"));
            if(post.getMember().getMemberId().equals(member.getMemberId())){
                throw new IllegalArgumentException("자신의 글에 좋아요 할 수 없습니다.");
            }
            Optional<ThumbUp> result = thumbUpRepository.findByPostAndMember(post, member);
            if(result.isPresent()){
                throw new IllegalArgumentException("이미 좋아요 한 글입니다.");
            }
            return thumbUpRepository.save(
                    ThumbUp.builder()
                            .post(post)
                            .member(member)
                            .build()
            );
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
        catch(Exception e){
            log.error(e);
            return null;
        }
    }

    @Override
    public boolean removeThumbUp(Integer postId, String memberId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));
            Optional<ThumbUp> result = thumbUpRepository.findByPostAndMember(post, member);
            if (result.isEmpty()) {
                throw new IllegalArgumentException("취소할 내역이 없습니다.");
            }
            thumbUpRepository.delete(result.get());
            return !thumbUpRepository.existsById(result.get().getThumbUpId());
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch(Exception e){
            log.error(e);
            return false;
        }
    }

    @Override
    public boolean isExist(Integer postId, String memberId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));
            Optional<ThumbUp> result = thumbUpRepository.findByPostAndMember(post, member);
            return result.isPresent();
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch(Exception e){
            log.error(e);
            return false;
        }
    }

    @Override
    public Integer getThumbUpCount(Integer postId) {
        try{
            Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
            return thumbUpRepository.findByPost(post).size();
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch(Exception e){
            log.error(e);
            return null;
        }
    }
}
