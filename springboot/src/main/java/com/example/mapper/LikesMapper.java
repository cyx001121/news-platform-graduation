package com.example.mapper;

import com.example.entity.Likes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikesMapper {
    Likes selectUserLikes(Likes likes);

    void insert(Likes likes);

    void deleteById(Integer id);

    int selectByFidAndModule(@Param("fid") Integer fid,@Param("module") String module);
    //@Param("fid")和@Param("module")用于为方法的参数 fid 和 module 分配具名参数，以便在 SQL 查询语句中使用
}
