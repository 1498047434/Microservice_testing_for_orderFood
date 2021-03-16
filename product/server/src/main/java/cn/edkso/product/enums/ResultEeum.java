package cn.edkso.product.enums;

import lombok.Getter;

@Getter
public enum ResultEeum {
    PRODUCT_NOT_EXIST(1, "商品不存在"),
    PRODUCT_STOCK_ERROR(1, "库存不足"),
        ;

    private Integer code;
    private String message;

    ResultEeum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
