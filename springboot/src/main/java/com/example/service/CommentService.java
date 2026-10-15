package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 新增
     */
    public void add(Comment comment) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())){//只有用户才能评论
            comment.setUserId(currentUser.getId());
        }
        comment.setTime(DateUtil.now());
        commentMapper.insert(comment);//先插入数据拿到主键id

        if (comment.getRootId() ==null) {
            comment.setRootId(comment.getId());
            commentMapper.updateById(comment);//更新rootid
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 查询前台评论
     */
    public List<Comment> selectForUser(Comment comment) {
        List<Comment> commentList=commentMapper.selectForUser(comment); //查询一级评论
        for (Comment c : commentList) {//查询回复列表,设置一级评论对象c
            Comment param = new Comment();//查询一级评论列表
            param.setRootId(c.getId());//设置rootId 为每个c的id
            List<Comment> children = this.selectAll(param);
            children = children.stream().filter(
                    child -> !child.getId().equals(c.getId())).collect(Collectors.toList());//排除掉其中的最外层节点即一级评论本身
            c.setChildren(children);//将过滤后的回复列表设置为当前一级评论对象的子评论列表
        }
        return commentList;
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

    public Integer selectCount(Integer fid, String module) {

        return commentMapper.selectCount(fid,module);
    }
}
