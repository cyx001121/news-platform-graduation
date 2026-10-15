package com.example.mapper;

import com.example.entity.Comment;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 新增
     */
    int insert(Comment comment);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Comment comment);

    /**
     * 根据ID查询
     */
    Comment selectById(Integer id);

    /**
     * 查询所有
     */
    List<Comment> selectAll(Comment comment);

    /**
     * 查询前台评论
     */
    List<Comment> selectForUser(Comment comment);

    @Select("select count(*) from comment where fid=#{fid} and module=#{module}")
    Integer selectCount(Integer fid, String module);
}
