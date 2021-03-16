package cn.edkso.order.controller;


import cn.edkso.order.VO.ResultVO;
import cn.edkso.order.converter.OrderForm2OrderDTOConverter;
import cn.edkso.order.dataobject.ProductInfo;
import cn.edkso.order.dto.CartDTO;
import cn.edkso.order.dto.OrderDTO;
import cn.edkso.order.enums.ResultEnum;
import cn.edkso.order.exception.OrderException;
import cn.edkso.order.form.OrderForm;
import cn.edkso.order.service.OrderService;
import cn.edkso.order.utils.ResultVOUtil;
import cn.edkso.product.client.ProductClient;
import cn.edkso.product.common.ProductInfoOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductClient productClient;
    /**
     * 1. 参数校验
     * 2. 查询商品信息（调用商品服务）
     * 3. 计算总价
     * 4. 扣库存（调用商品服务）
     * 5. 订单入库
     */
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(
            @Valid OrderForm orderForm,
            BindingResult bindingResult
            ){
        if (bindingResult.hasErrors()){
            log.error("【创建订单】参数不正确，orderForm={}",orderForm);
            throw new OrderException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        //orderForm -> orderDTO
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);

        //还要再做一次购物车不为空判断，Valid只能判断出items不为空，items中也可能被恶意存放了其他信息
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【创建订单】购物车信息为空");
            throw  new OrderException(ResultEnum.CART_EMPTY);
        }

        OrderDTO result = orderService.create(orderDTO);
        Map<String,String> map = new HashMap<>();

        map.put("orderId", result.getOrderId());
        return ResultVOUtil.success(map);
    }

    @GetMapping("/getProductList")
    public String getProductList(){
        List<ProductInfoOutput> productInfos = productClient.listForOrder(Arrays.asList("10001"));
        log.info("res={}",productInfos);
        return "ok";
    }

//    @GetMapping("/productDecreaseStock")
//    public String productDecreaseStock(){
//        productClient.decreaseStock(Arrays.asList(new CartDTO("10001",2)));
//        return "ok";
//    }
}
