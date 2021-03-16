package cn.edkso.order.converter;

import cn.edkso.order.dataobject.OrderDetail;
import cn.edkso.order.dto.OrderDTO;
import cn.edkso.order.form.OrderForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());

        List<OrderDetail> orderDetailList = new ArrayList<>();

        Gson gson = new Gson();
        orderDetailList = gson.fromJson(orderForm.getItems(),
                new TypeToken<List<OrderDetail>>() {
                }.getType());

        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }
}
