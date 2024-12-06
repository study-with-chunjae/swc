package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PostRegisterDTO;

public interface PostServiceIf {
    public Post registerPost(PostRegisterDTO postRegisterDTO, String memberId);
}
