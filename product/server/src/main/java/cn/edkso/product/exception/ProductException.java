package cn.edkso.product.exception;

import cn.edkso.product.enums.ResultEeum;

public class ProductException extends RuntimeException{

    private Integer code;

    public ProductException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public ProductException(ResultEeum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
