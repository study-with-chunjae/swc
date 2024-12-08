package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.ThumbUp;

public interface ThumbUpServiceIf {
    ThumbUp addThumbUp (Integer postId, String memberId);
    boolean removeThumbUp(Integer postId, String memberId);
}
