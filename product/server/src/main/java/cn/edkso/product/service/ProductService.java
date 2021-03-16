package cn.edkso.product.service;

import cn.edkso.product.dto.CartDTO;
import cn.edkso.product.entity.ProductInfo;

import java.util.List;

public interface ProductService {

    /**
     * 查询所有在架商品
     * @return
     */
    public List<ProductInfo>  findUpAll();

    /**
     * 查询商品列表
     * @param productIdList
     * @return
     */
    List<ProductInfo> findList(List<String> productIdList);


    void decreaseStock(List<CartDTO> cartDTOList);
}
