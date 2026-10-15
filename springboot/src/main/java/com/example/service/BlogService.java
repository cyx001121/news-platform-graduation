package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.common.enums.LikesModuleEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.BlogMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 新闻信息业务处理
 **/
@Service
public class BlogService {

    @Resource
     UserService userService;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    CollectService collectService;
    @Resource
    LikesService likesService;
    /**
     * 新增
     */
    public void add(Blog blog) {
        blog.setDate(DateUtil.today());
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())){
            blog.setUserId(currentUser.getId());
        }
        blogMapper.insert(blog);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        blogMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            blogMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Blog blog) {
        blogMapper.updateById(blog);
    }

    /**
     * 根据ID查询
     */
    public Blog selectById(Integer id) {
        Blog blog=blogMapper.selectById(id);
        User user =userService.selectById(blog.getUserId());
        List<Blog> userBlogList= blogMapper.selectUserBlog(user.getId());
        user.setBlogCount(userBlogList.size());//用户点赞数
        //查询该用户收到的点赞
        int userLikesCount = 0;
        int userCollectCount = 0;
        for (Blog b:userBlogList){
            Integer fid = b.getId();
            int likesCount = likesService.selectByFidAndModule(fid,LikesModuleEnum.Blog.getValue());
            userLikesCount +=likesCount;

            int collectCount = likesService.selectByFidAndModule(fid,LikesModuleEnum.Blog.getValue());
            userCollectCount +=collectCount;
        }
        user.setLikesCount(userLikesCount);
        user.setCollectCount(userCollectCount);



        blog.setUser(user);//设置作者信息
        //查询当前新闻的点赞数据
        int likesCount =likesService.selectByFidAndModule(id, LikesModuleEnum.Blog.getValue());
        blog.setLikesCount(likesCount);
        Likes userLikes =likesService.selectUserLikes(id,LikesModuleEnum.Blog.getValue());
        blog.setUserLike(userLikes !=null);//不是null点过赞

        //查询收藏
        int collectCount =collectService.selectByFidAndModule(id, LikesModuleEnum.Blog.getValue());//查询当前新闻的点赞数据
        blog.setCollectCount(collectCount);
        Collect userCollect =collectService.selectUserCollect(id,LikesModuleEnum.Blog.getValue());
        blog.setUserCollect(userCollect !=null);//不是null点过赞


        //更新新闻浏览量
        blog.setReadCount(blog.getReadCount()+1);
        this.updateById(blog);
        return blog;
    }

    /**
     * 查询所有
     */
    public List<Blog> selectAll(Blog blog) {
        return blogMapper.selectAll(blog);
    }

    /**
     * 分页查询
     */
    public PageInfo<Blog> selectPage(Blog blog, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectAll(blog);
        for (Blog b :list){
            int likesCount =likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.Blog.getValue());//查询当前新闻的点赞数据
            b.setLikesCount(likesCount);
        }
        return PageInfo.of(list);
    }

    public List<Blog> selectTop() {
        List<Blog> blogList=this.selectAll(null);
        blogList = blogList.stream().sorted((b1,b2)->b2.getReadCount().compareTo(b1.getReadCount()))//对比阅读数量进行降序排列
                .limit(50)
                .collect(Collectors.toList());//使用 collect() 方法将流中的元素收集到一个blogList集合中
        return blogList;
    }

    public Set<Blog> selectRecommend(Integer blogId) {
        Blog blog =this.selectById(blogId);
        String tags = blog.getTags();
        Set<Blog> blogSet =new HashSet<>();//去重
        if (ObjectUtil.isNotEmpty(tags)){
            List<Blog> blogList= this.selectAll(null);//不指定查询条件、
            JSONArray tagsArr = JSONUtil.parseArray(tags);//通过JSONUtil工具类将tags解析成JSON形式存入tagsArr
            for (Object tag : tagsArr){
                Set<Blog> collect = blogList.stream()//通过流式筛选包含标签的其它新闻
                        .filter(b->b.getTags().contains(tag.toString()) && !blogId.equals(b.getId()))//b.getTags().contains(tag.toString())：这部分判断条件表示当前新闻的标签是否包含指定的标签
                        .collect(Collectors.toSet());//将不为本篇新闻但有相同tag的新闻收集
                blogSet.addAll(collect);//将 collect 集合中的元素添加到 blogSet 集合中。
            }
        }
        blogSet= blogSet.stream().limit(5).collect(Collectors.toSet());//前5个元素作为推荐的新闻集合，并将其收集到一个新的集合中返回
        blogSet.forEach(b ->{
            int likesCount =likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.Blog.getValue());//查询当前新闻的点赞数据
            b.setLikesCount(likesCount);
        });
        return blogSet;
    }

    public PageInfo<Blog> selectUser(Blog blog, Integer pageNum, Integer pageSize) {
        Account currentUser=TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())){
            blog.setUserId(currentUser.getId());
        }
        return this.selectPage(blog,pageNum,pageSize);
    }

    public PageInfo<Blog> selectLike(Blog blog, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())) {
            blog.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectLike(blog);
        PageInfo<Blog> pageInfo = PageInfo.of(list);
        List<Blog> blogList = pageInfo.getList();
        for (Blog b : blogList) {
            int likesCount = likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.Blog.getValue());
            b.setLikesCount(likesCount);
        }
        return pageInfo;
    }

    public PageInfo<Blog> selectCollect(Blog blog, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())) {
            blog.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectCollect(blog);
        PageInfo<Blog> pageInfo = PageInfo.of(list);
        List<Blog> blogList = pageInfo.getList();
        for (Blog b : blogList) {
            int likesCount = likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.Blog.getValue());
            b.setLikesCount(likesCount);
        }
        return pageInfo;
    }

    public PageInfo<Blog> selectComment(Blog blog, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.User.name().equals(currentUser.getRole())) {
            blog.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectComment(blog);
        PageInfo<Blog> pageInfo = PageInfo.of(list);
        List<Blog> blogList = pageInfo.getList();
        for (Blog b : blogList) {
            int likesCount = likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.Blog.getValue());
            b.setLikesCount(likesCount);
        }
        return pageInfo;
    }
}