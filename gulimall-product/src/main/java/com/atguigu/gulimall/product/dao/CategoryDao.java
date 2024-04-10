package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zczjyq
 * @email 937473303@qq.com
 * @date 2024-04-02 16:43:01
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
