package net.fullstack7.swc.service;

import net.fullstack7.swc.domain.Share;
import net.fullstack7.swc.dto.ShareDTO;

public interface ShareServiceIf {
    Share addShare(ShareDTO shareDTO);
    boolean removeShare(ShareDTO shareDTO);
}
