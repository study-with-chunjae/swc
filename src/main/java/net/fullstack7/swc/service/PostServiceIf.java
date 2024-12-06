package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PostRegisterDTO;
import net.fullstack7.swc.dto.PostViewDTO;

public interface PostServiceIf {
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId);
    public PostViewDTO viewPost(int postId);
}
