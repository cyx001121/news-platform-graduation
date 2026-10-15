package com.example.mapper;


import com.example.entity.Activity;
import com.example.entity.Category;
import com.example.entity.Notice;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ActivityMapper {

    void add(Activity activity);

    void insert(Activity activity);

    void deleteById(Integer id);


    void updateById(Activity activity);

    Activity selectById(Integer id);

    List<Activity> selectAll(Activity activity);

    @Update("update activity set read_count = read_count + 1 where id = #{activityId}")
    void updateReadCount(Integer activityId);

    List<Activity> selectUser(Activity activity);

    List<Activity> selectLike(Activity activity);

    List<Activity> selectCollect(Activity activity);

    List<Activity> selectComment(Activity activity);
}
