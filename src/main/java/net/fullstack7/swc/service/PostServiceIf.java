package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Member;
import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface PostServiceIf {
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId);
    public PostDTO viewPost(int postId);
    public Post viewPost2(int postId);
    public List<PostMainDTO> mainPost(LocalDateTime createdAt, String memberId, Integer todayType);
    public PageDTO<PostDTO> sortAndSearch(PageDTO<PostDTO> pageDTO, String memberId);
    public PageDTO<PostDTO> sortAndSearchShare(PageDTO<PostDTO> pageDTO, String memberId, String type);
    public Post modifyPost(PostModifyDTO postModifyDTO, String memberId);
    public boolean deletePost(int postId, String memberId);
}
