package cn.edkso.product.service.impl;


import cn.edkso.product.dto.CartDTO;
import cn.edkso.product.entity.ProductInfo;
import cn.edkso.product.enums.ProductStatusEnum;
import cn.edkso.product.enums.ResultEeum;
import cn.edkso.product.exception.ProductException;
import cn.edkso.product.repositry.ProductInfoRepository;
import cn.edkso.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public List<ProductInfo> findUpAll() {
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public List<ProductInfo> findList(List<String> productIdList) {
        return productInfoRepository.findByProductIdIn(productIdList);
    }

    @Override
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            Optional<ProductInfo> productInfoOptional = productInfoRepository.findById(cartDTO.getProductId());
            if (!productInfoOptional.isPresent()){
                throw new ProductException(ResultEeum.PRODUCT_NOT_EXIST);
            }

            ProductInfo productInfo = productInfoOptional.get();

            //库存是否足够
            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result < 0){
                throw new ProductException(ResultEeum.PRODUCT_NOT_EXIST);
            }

            productInfo.setProductStock(result);
            productInfoRepository.save(productInfo);
        }
    }
}
