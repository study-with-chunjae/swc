package net.fullstack7.swc.repository.search;

import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSearch {
    Page<Post> searchAndSort(Pageable pageable, String searchField, String searchValue, String sortField,
                             String sortDirection, String searchDateBegin, String searchDateEnd, String memberId);
}