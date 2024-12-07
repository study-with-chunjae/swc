package net.fullstack7.swc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.domain.Friend;
import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.domain.Share;
import net.fullstack7.swc.dto.ShareDTO;
import net.fullstack7.swc.repository.FriendRepository;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.repository.PostRepository;
import net.fullstack7.swc.repository.ShareRepository;
import net.fullstack7.swc.util.LogUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ShareServiceImpl implements ShareServiceIf{
    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final LocalDateTime NOW = LocalDateTime.now();

    @Override
    public Share addShare(ShareDTO shareDTO) {
        LogUtil.logLine("ShareService addShare");
        try {
            Post post = postRepository.findById(shareDTO.getPostId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 학습입니다."));
            Member member = memberRepository.findById(shareDTO.getMemberId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));
            if(post.getMember().getMemberId().equals(member.getMemberId())) {
                throw new IllegalArgumentException("자신의 글은 공유할 수 없습니다.");
            }
            Friend friend = friendRepository.findByRequesterAndReceiver(post.getMember(),member).orElseThrow(()->new IllegalArgumentException("친구가 아닌 회원에게 공유할 수 없습니다."));
            Optional<Share> shareOptional = shareRepository.findByPostAndMember(post,member);
            if(shareOptional.isPresent()) {
                throw new IllegalArgumentException("이미 공유한 회원입니다.");
            }

            return shareRepository.save(
                    Share.builder()
                            .post(post)
                            .member(member)
                            .createdAt(NOW)
                            .build()
            );
        }catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public boolean removeShare(ShareDTO shareDTO) {
        LogUtil.logLine("ShareService removeShare");
        try {
            Post post = postRepository.findById(shareDTO.getPostId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학습입니다."));
            Member member = memberRepository.findById(shareDTO.getMemberId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            Share share = shareRepository.findByPostAndMember(post,member).orElseThrow(()->new IllegalArgumentException("해당하는 공유내역이 없습니다."));
            shareRepository.delete(share);
            return !shareRepository.existsById(share.getShareId());
        }catch (Exception e) {
            log.error(e);
            return false;
        }
    }
}
