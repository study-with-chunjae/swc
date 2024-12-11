package net.fullstack7.swc.mapper;
import net.fullstack7.swc.dto.FriendListDTO;
import net.fullstack7.swc.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<FriendListDTO> friendList(@Param("pageDTO") PageDTO<FriendListDTO> pageDTO, @Param("memberId") String memberId);
    int totalCount(@Param("pageDTO") PageDTO<FriendListDTO> pageDTO, @Param("memberId") String memberId);
}
