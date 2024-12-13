package net.fullstack7.swc.mapper;

import net.fullstack7.swc.domain.Post;
import net.fullstack7.swc.dto.PageDTO;
import net.fullstack7.swc.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    int totalCount(@Param("searchField") String searchField,
                   @Param("searchValue") String searchValue,
                   @Param("sortField") String sortField,
                   @Param("sortDirection")String sortDirection,
                   @Param("searchDateBegin") String searchDateBegin,
                   @Param("searchDateEnd") String searchDateEnd,
                   @Param("memberId") String memberId
                   );
    List<PostDTO> postList(Map<String,Object> map);
    int sharedTotalCount(@Param("pageDTO")PageDTO<PostDTO> pageDTO, @Param("memberId")String memberId, @Param("type")String type);
    List<PostDTO> getSharedList(@Param("pageDTO")PageDTO<PostDTO> pageDTO, @Param("memberId")String memberId, @Param("type")String type);

}
