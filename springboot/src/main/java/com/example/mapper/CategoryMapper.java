package com.example.mapper;

import com.example.entity.Category;
import com.example.entity.Notice;

import java.util.List;

public interface CategoryMapper {


    void add(Category category);

    void insert(Category category);

    void deleteById(Integer id);


    void updateById(Category category);

    Category selectById(Integer id);

    List<Category> selectAll(Category category);
}
