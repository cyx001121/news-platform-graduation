package com.example.service;

import com.example.entity.Account;
import com.example.entity.Likes;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import org.apache.el.parser.Token;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikesService {
    @Resource
    LikesMapper likesMapper;


    public void set(Likes likes) {
        Account currentUser = TokenUtils.getCurrentUser();//获取当前用户信息
       // System.out.println(currentUser+"currentUser++++++");
        likes.setUserId(currentUser.getId());//将用户id设置进当前点赞信息
        Likes dbLikes = likesMapper.selectUserLikes(likes);//查询数据库中是否存在相同的点赞信息，如有dblLikes将为非空，没有为null
        if (dbLikes == null){
            likesMapper.insert(likes);//不存在相同点赞则点赞
        }else{
            likesMapper.deleteById(dbLikes.getId());//存在则取消点赞
        }
    }

    /**
     * 查询当前用户是否点赞过此新闻
     */
    public Likes selectUserLikes(Integer fid,String module){
        Account currentUser = TokenUtils.getCurrentUser();
        Likes likes =new Likes();
        likes.setUserId(currentUser.getId());//将当前用户id设置到likes对象中,查询的是当前用户的点赞信息
        likes.setFid(fid);//查询的是特定对象的点赞信息
        likes.setModule(module);//表示查询的是特定模块的点赞信息
        return likesMapper.selectUserLikes(likes);
    }
    public int selectByFidAndModule(Integer fid,String module){
        return likesMapper.selectByFidAndModule(fid,module);
    }

}
