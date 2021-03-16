package cn.edkso.order.service.impl;


import cn.edkso.order.dataobject.OrderDetail;
import cn.edkso.order.dataobject.OrderMaster;
import cn.edkso.order.dataobject.ProductInfo;
import cn.edkso.order.dto.CartDTO;
import cn.edkso.order.dto.OrderDTO;
import cn.edkso.order.enums.OrderStatusEnum;
import cn.edkso.order.enums.PayStatusEnum;
import cn.edkso.order.repository.OrderDetailRepository;
import cn.edkso.order.repository.OrderMasterRepository;
import cn.edkso.order.service.OrderService;
import cn.edkso.order.utils.KeyUtil;
import cn.edkso.product.client.ProductClient;
import cn.edkso.product.common.DecreaseStockInput;
import cn.edkso.product.common.ProductInfoOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private ProductClient productClient;


    @Override
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId = KeyUtil.genUniqeKey();

        // 查询商品信息（调用商品服务）
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());

        List<ProductInfoOutput> productInfoList = productClient.listForOrder(productIdList);

        // 计算总价
        BigDecimal orderAmout = new BigDecimal(BigInteger.ZERO);
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            for (ProductInfoOutput productInfo : productInfoList) {
                if (productInfo.getProductId().equals(orderDetail.getProductId())){
                    orderAmout = orderAmout
                            .add(productInfo.getProductPrice()
                            .multiply(new BigDecimal(orderDetail.getProductQuantity())));
                    BeanUtils.copyProperties(productInfo,orderDetail);

                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(KeyUtil.genUniqeKey());

                    //订单详情入库
                    orderDetailRepository.save(orderDetail);
                }
            }
        }


        // 扣库存（调用商品服务）
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockInputList);


        //订单入库
        OrderMaster orderMaster = new OrderMaster();

        orderDTO.setOrderId(orderId);

        BeanUtils.copyProperties(orderDTO,orderMaster);

        orderMaster.setOrderAmount(new BigDecimal(5));
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.NEW.getCode());

        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }
}
