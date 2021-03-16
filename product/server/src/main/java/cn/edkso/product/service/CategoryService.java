package cn.edkso.product.service;


import cn.edkso.product.entity.ProductCategory;

import java.util.List;

public interface CategoryService {

    /**
     * 通过Type查询商品分类
     * @param categoryTypeList
     * @return
     */
    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);
}
